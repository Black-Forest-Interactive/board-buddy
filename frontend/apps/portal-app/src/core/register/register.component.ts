import {Component, inject} from '@angular/core'
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms'
import {Router} from '@angular/router'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatButtonModule} from '@angular/material/button'
import {MatCardModule} from '@angular/material/card'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {CreatePlayerRequest, PlayerService} from '@board-buddy/portal'

@Component({
  selector: 'portal-register',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatCardModule, MatIconModule, TranslatePipe],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private playerService = inject(PlayerService)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly nameControl = new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(30)])

  submit() {
    if (this.nameControl.invalid) return
    this.playerService.createPlayer(new CreatePlayerRequest(this.nameControl.value!)).subscribe({
      next: (player) => {
        this.playerService.setPlayerId(player.id)
        this.router.navigate(['/home'])
      },
      error: () => this.translate.get('register.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
