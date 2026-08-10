import {ChangeDetectionStrategy, Component, computed, inject, resource, signal} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatListModule} from '@angular/material/list'
import {MatIconModule} from '@angular/material/icon'
import {MatSelectModule} from '@angular/material/select'
import {MatFormFieldModule} from '@angular/material/form-field'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PlayerService, WorkflowService} from '@board-buddy/admin'
import {Nation, Player, WorkflowAssignPlayerRequest} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-session-assign-dialog',
  imports: [MatDialogModule, MatListModule, MatButtonModule, MatIconModule, MatSelectModule, MatFormFieldModule, TranslatePipe],
  templateUrl: './session-assign-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionAssignDialogComponent {
  private workflowService = inject(WorkflowService)
  private playerService = inject(PlayerService)
  private dialogRef = inject(MatDialogRef<SessionAssignDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private data: {sessionKey: string, participants: Player[]} = inject(MAT_DIALOG_DATA)

  private allResource = resource({loader: () => toPromise(this.playerService.getPlayers(0, 100))})
  private nationResource = resource({loader: () => toPromise(this.workflowService.getAvailableNations(this.data.sessionKey))})

  readonly nations = computed(() => this.nationResource.value() ?? [])
  readonly selectedNation = signal<number | null>(null)

  readonly available = computed(() => {
    const participantIds = new Set(this.data.participants.map(p => p.id))
    return (this.allResource.value()?.content ?? []).filter(p => !participantIds.has(p.id))
  })

  assign(player: Player) {
    const nationId = this.selectedNation()
    if (!nationId) return
    const request = new WorkflowAssignPlayerRequest(player.id, nationId)
    this.workflowService.assignPlayer(this.data.sessionKey, request).subscribe({
      next: () => {
        this.translate.get('session.message.playerAssigned').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
