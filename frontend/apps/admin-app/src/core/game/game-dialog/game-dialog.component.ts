import {ChangeDetectionStrategy, Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameService} from '@board-buddy/admin'
import {Game, GameChangeRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-game-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, TranslatePipe],
  templateUrl: './game-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameDialogComponent {
  private service = inject(GameService)
  private dialogRef = inject(MatDialogRef<GameDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly game: Game | null = inject(MAT_DIALOG_DATA, {optional: true})

  readonly form = new FormGroup({
    name: new FormControl(this.game?.name ?? '', Validators.required),
    description: new FormControl(this.game?.description ?? '', Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const request = new GameChangeRequest(this.form.value.name!, this.form.value.description!)
    const call = this.game ? this.service.update(this.game.id, request) : this.service.create(request)
    call.subscribe({
      next: () => {
        const key = this.game ? 'game.message.updated' : 'game.message.created'
        this.translate.get(key).subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('game.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
