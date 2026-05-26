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
import {NationService} from '@board-buddy/admin'
import {GovernmentType, NationEffect} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {NationEffectDialogComponent} from '../nation-effect-dialog/nation-effect-dialog.component'

@Component({
  selector: 'admin-nation-detail',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, TranslatePipe],
  templateUrl: './nation-detail.component.html',
})
export class NationDetailComponent {
  private service = inject(NationService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private id = toSignal(this.route.paramMap.pipe(map(p => Number(p.get('id')))))

  private nationResource = resource({
    params: this.id,
    loader: (p) => p.params ? toPromise(this.service.getNation(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  readonly nation = computed(() => this.nationResource.value())
  readonly name = computed(() => this.nation()?.name ?? '')
  readonly imageUrl = computed(() => this.nation()?.imageUrl ?? '')
  readonly effects = computed(() => this.nation()?.effect ?? [])

  readonly effectColumns = ['governmentType', 'actions']

  openAddEffect() {
    const id = this.id()
    if (!id) return
    this.dialog.open(NationEffectDialogComponent, {data: id})
      .afterClosed().subscribe(updated => { if (updated) this.nationResource.set(updated) })
  }

  revokeEffect(effect: NationEffect) {
    const id = this.id()
    if (!id) return
    this.service.revokeInitialGovernment(id, effect.type as GovernmentType).subscribe({
      next: (updated) => {
        this.nationResource.set(updated)
        this.translate.get('nation.effect.message.removed').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('nation.effect.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
