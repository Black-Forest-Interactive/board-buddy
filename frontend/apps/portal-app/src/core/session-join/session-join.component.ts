import {Component, computed, inject} from '@angular/core'
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
import {NationType} from '@board-buddy/core'
import {PlayerService, PortalJoinSessionRequest, PortalSessionService} from '@board-buddy/portal'

@Component({
  selector: 'portal-session-join',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule, MatCardModule, TranslatePipe],
  templateUrl: './session-join.component.html',
})
export class SessionJoinComponent {
  private sessionService = inject(PortalSessionService)
  private playerService = inject(PlayerService)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly nations = Object.values(NationType)

  readonly form = new FormGroup({
    key: new FormControl('', [Validators.required, Validators.minLength(3)]),
    nation: new FormControl<NationType | null>(null, Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const {key, nation} = this.form.value
    this.sessionService.joinSession(key!, new PortalJoinSessionRequest(nation!)).subscribe({
      next: (workflow) => this.router.navigate(['/session', workflow.id]),
      error: () => this.translate.get('session.join.message.error').subscribe(t => this.toast.error(t))
    })
  }

  back() { this.router.navigate(['/home']) }
}
