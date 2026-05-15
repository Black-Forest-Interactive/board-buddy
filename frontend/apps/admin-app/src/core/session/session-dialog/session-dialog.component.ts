import {Component, computed, effect, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameService, PlayerService, WorkflowService} from '@board-buddy/admin'
import {Game, NationType, Player, RuleSet, WorkflowCreateRequest} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-session-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './session-dialog.component.html',
})
export class SessionDialogComponent {
  private workflowService = inject(WorkflowService)
  private dialogRef = inject(MatDialogRef<SessionDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private playerService = inject(PlayerService)
  private gameService = inject(GameService)

  private playersResource = resource({loader: () => toPromise(this.playerService.getPlayers(0, 100))})
  private gamesResource = resource({loader: () => toPromise(this.gameService.getGames(0, 100))})

  readonly players = computed(() => this.playersResource.value()?.content ?? [])
  readonly games = computed(() => this.gamesResource.value()?.content ?? [])
  readonly nations = Object.values(NationType)

  readonly form = new FormGroup({
    name: new FormControl('', Validators.required),
    host: new FormControl<Player | null>(null, Validators.required),
    nation: new FormControl<NationType>(NationType.AMERICA, Validators.required),
    game: new FormControl<Game | null>(null, Validators.required),
    ruleSet: new FormControl<RuleSet | null>({value: null, disabled: true}, Validators.required),
  })

  private selectedGame = toSignal(this.form.controls.game.valueChanges, {initialValue: null})

  readonly ruleSets = computed<RuleSet[]>(() =>
    this.selectedGame()?.ruleSets ?? []
  )

  constructor() {
    effect(() => {
      const game = this.selectedGame()
      this.form.controls.ruleSet.reset(null)
      if (game && game.ruleSets.length > 0) {
        this.form.controls.ruleSet.enable()
      } else {
        this.form.controls.ruleSet.disable()
      }
    })
  }

  submit() {
    if (this.form.invalid) return
    const v = this.form.getRawValue()
    const request = new WorkflowCreateRequest(v.name!, v.host!.id, v.game!.id, v.ruleSet!.id, v.nation!)
    this.workflowService.create(request).subscribe({
      next: () => {
        this.translate.get('session.message.created').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
