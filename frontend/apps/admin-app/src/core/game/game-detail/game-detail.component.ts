import {Component, computed, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute} from '@angular/router'
import {map} from 'rxjs'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameService} from '@board-buddy/admin'
import {RuleSet} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {GameAssignDialogComponent} from '../game-assign-dialog/game-assign-dialog.component'

@Component({
  selector: 'admin-game-detail',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, TranslatePipe],
  templateUrl: './game-detail.component.html',
})
export class GameDetailComponent {
  private service = inject(GameService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private id = toSignal(this.route.paramMap.pipe(map(p => Number(p.get('id')))))

  private gameResource = resource({
    params: this.id,
    loader: (p) => p.params ? toPromise(this.service.getGame(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  readonly game = computed(() => this.gameResource.value())
  readonly name = computed(() => this.game()?.name ?? '')
  readonly ruleSets = computed(() => this.game()?.ruleSets ?? [])
  readonly columns = ['name', 'actions']

  openAssign() {
    const id = this.id()
    if (!id) return
    this.dialog.open(GameAssignDialogComponent, {
      data: {gameId: id, assigned: this.ruleSets()}
    }).afterClosed().subscribe(saved => {if (saved) this.gameResource.reload()})
  }

  revoke(ruleSet: RuleSet) {
    const id = this.id()
    if (!id) return
    this.service.revokeRuleSet(id, ruleSet.id).subscribe({
      next: (updated) => {
        this.gameResource.set(updated)
        this.translate.get('game.message.ruleSetRevoked').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('game.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
