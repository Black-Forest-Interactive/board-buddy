import {Component, computed, inject, resource} from '@angular/core'
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
import {PlayerService, PortalJoinSessionRequest, PortalSessionService, PortalWorkflowService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'portal-session-join',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule, MatCardModule, TranslatePipe],
  templateUrl: './session-join.component.html',
})
export class SessionJoinComponent {
  private sessionService = inject(PortalSessionService)
  private workflowService = inject(PortalWorkflowService)
  private playerService = inject(PlayerService)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly form = new FormGroup({
    key: new FormControl('', [Validators.required, Validators.minLength(3)]),
    nationId: new FormControl<number | null>(null, Validators.required),
  })

  private key = toSignal(this.form.controls.key.valueChanges, {initialValue: ''})

  private nationResource = resource({
    params: this.key,
    loader: (p) => p.params && p.params.length >= 3 ? toPromise(this.workflowService.getAvailableNations(p.params), p.abortSignal) : Promise.resolve([])
  })

  readonly nations = computed(() => this.nationResource.value() ?? [])

  submit() {
    if (this.form.invalid) return
    const {key, nationId} = this.form.value
    this.sessionService.joinSession(key!, new PortalJoinSessionRequest(nationId!)).subscribe({
      next: (workflow) => this.router.navigate(['/session', workflow.id]),
      error: () => this.translate.get('session.join.message.error').subscribe(t => this.toast.error(t))
    })
  }

  back() { this.router.navigate(['/home']) }
}
