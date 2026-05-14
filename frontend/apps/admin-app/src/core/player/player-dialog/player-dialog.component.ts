import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PlayerService} from '@board-buddy/admin'
import {Player, PlayerChangeRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-player-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, TranslatePipe],
  templateUrl: './player-dialog.component.html',
})
export class PlayerDialogComponent {
  private service = inject(PlayerService)
  private dialogRef = inject(MatDialogRef<PlayerDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly player: Player | null = inject(MAT_DIALOG_DATA, {optional: true})

  readonly form = new FormGroup({
    name: new FormControl(this.player?.name ?? '', Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const request = new PlayerChangeRequest(this.form.value.name!)
    const call = this.player ? this.service.update(this.player.id, request) : this.service.create(request)
    call.subscribe({
      next: () => {
        const key = this.player ? 'player.message.updated' : 'player.message.created'
        this.translate.get(key).subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('player.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
