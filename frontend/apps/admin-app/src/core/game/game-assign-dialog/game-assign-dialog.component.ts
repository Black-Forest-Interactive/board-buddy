import {ChangeDetectionStrategy, Component, computed, inject, resource} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatListModule} from '@angular/material/list'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameService, RuleSetService} from '@board-buddy/admin'
import {RuleSet} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-game-assign-dialog',
  imports: [MatDialogModule, MatListModule, MatButtonModule, MatIconModule, TranslatePipe],
  templateUrl: './game-assign-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameAssignDialogComponent {
  private gameService = inject(GameService)
  private ruleSetService = inject(RuleSetService)
  private dialogRef = inject(MatDialogRef<GameAssignDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private data: {gameId: number, assigned: RuleSet[]} = inject(MAT_DIALOG_DATA)

  private allResource = resource({
    loader: () => toPromise(this.ruleSetService.getRuleSets(0, 100))
  })

  readonly available = computed(() => {
    const assignedIds = new Set(this.data.assigned.map(r => r.id))
    return (this.allResource.value()?.content ?? []).filter(r => !assignedIds.has(r.id))
  })

  assign(ruleSet: RuleSet) {
    this.gameService.assignRuleSet(this.data.gameId, ruleSet.id).subscribe({
      next: () => {
        this.translate.get('game.message.ruleSetAssigned').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('game.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
