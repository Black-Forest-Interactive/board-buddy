import {Component, computed, inject, resource} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatListModule} from '@angular/material/list'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PlayerService, SessionService} from '@board-buddy/admin'
import {Player} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-session-assign-dialog',
  imports: [MatDialogModule, MatListModule, MatButtonModule, MatIconModule, TranslatePipe],
  templateUrl: './session-assign-dialog.component.html',
})
export class SessionAssignDialogComponent {
  private sessionService = inject(SessionService)
  private playerService = inject(PlayerService)
  private dialogRef = inject(MatDialogRef<SessionAssignDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private data: {sessionId: number, participants: Player[]} = inject(MAT_DIALOG_DATA)

  private allResource = resource({
    loader: () => toPromise(this.playerService.getPlayers(0, 100))
  })

  readonly available = computed(() => {
    const participantIds = new Set(this.data.participants.map(p => p.id))
    return (this.allResource.value()?.content ?? []).filter(p => !participantIds.has(p.id))
  })

  assign(player: Player) {
    this.sessionService.assignPlayer(this.data.sessionId, player.id).subscribe({
      next: () => {
        this.translate.get('session.message.playerAssigned').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
