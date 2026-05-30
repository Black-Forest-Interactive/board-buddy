import {Component, computed, effect, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {Router} from '@angular/router'
import {MatButtonModule} from '@angular/material/button'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatSelectModule} from '@angular/material/select'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {Nation, RuleSet} from '@board-buddy/core'
import {PlayerService, PortalCreateSessionRequest, PortalSessionService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'portal-session-create',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule, MatCardModule, TranslatePipe],
  templateUrl: './session-create.component.html',
})
export class SessionCreateComponent {
  private sessionService = inject(PortalSessionService)
  private playerService = inject(PlayerService)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private gamesResource = resource({loader: (p) => toPromise(this.sessionService.getGames(), p.abortSignal)})

  readonly games = computed(() => this.gamesResource.value() ?? [])

  readonly form = new FormGroup({
    name: new FormControl('', [Validators.required, Validators.minLength(2)]),
    gameId: new FormControl<number | null>(null, Validators.required),
    ruleSetId: new FormControl<number | null>({value: null, disabled: true}, Validators.required),
    nationId: new FormControl<number | null>({value: null, disabled: true}, Validators.required),
  })

  private selectedGameId = toSignal(this.form.controls.gameId.valueChanges, {initialValue: null})
  private selectedRuleSetId = toSignal(this.form.controls.ruleSetId.valueChanges, {initialValue: null})

  readonly ruleSets = computed<RuleSet[]>(() => {
    const id = this.selectedGameId()
    return this.games().find(g => g.id === id)?.ruleSets ?? []
  })

  readonly nations = computed<Nation[]>(() => {
    const id = this.selectedRuleSetId()
    return this.ruleSets().find(rs => rs.id === id)?.nations ?? []
  })

  constructor() {
    effect(() => {
      const games = this.games()
      if (games.length === 1) this.form.controls.gameId.setValue(games[0].id)
    })

    effect(() => {
      const ruleSets = this.ruleSets()
      this.form.controls.ruleSetId.reset(ruleSets.length === 1 ? ruleSets[0].id : null)
      ruleSets.length > 0 ? this.form.controls.ruleSetId.enable() : this.form.controls.ruleSetId.disable()
    })

    effect(() => {
      const nations = this.nations()
      this.form.controls.nationId.reset(nations.length === 1 ? nations[0].id : null)
      nations.length > 0 ? this.form.controls.nationId.enable() : this.form.controls.nationId.disable()
    })
  }

  submit() {
    if (this.form.invalid) return
    const v = this.form.getRawValue()
    const playerId = this.playerService.getPlayerId()
    if (!playerId) { this.router.navigate(['/register']); return }
    const request = new PortalCreateSessionRequest(v.name!, v.gameId!, v.ruleSetId!, v.nationId!)
    this.sessionService.createSession(request).subscribe({
      next: (workflow) => this.router.navigate(['/session', workflow.id]),
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  back() { this.router.navigate(['/home']) }
}
