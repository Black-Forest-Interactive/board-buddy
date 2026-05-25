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
import {TechnologyService} from '@board-buddy/admin'
import {TechnologyEffect, UnitType} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {TechnologyEffectDialogComponent} from '../technology-effect-dialog/technology-effect-dialog.component'

@Component({
  selector: 'admin-technology-detail',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, TranslatePipe],
  templateUrl: './technology-detail.component.html',
})
export class TechnologyDetailComponent {
  private service = inject(TechnologyService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private id = toSignal(this.route.paramMap.pipe(map(p => Number(p.get('id')))))

  private technologyResource = resource({
    params: this.id,
    loader: (p) => p.params ? toPromise(this.service.getTechnology(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  readonly technology = computed(() => this.technologyResource.value())
  readonly name = computed(() => this.technology()?.name ?? '')
  readonly effects = computed(() => this.technology()?.effect ?? [])

  readonly effectColumns = ['unitType', 'unitLevel', 'actions']

  openAddEffect() {
    const id = this.id()
    if (!id) return
    this.dialog.open(TechnologyEffectDialogComponent, {data: id})
      .afterClosed().subscribe(updated => { if (updated) this.technologyResource.set(updated) })
  }

  revokeEffect(effect: TechnologyEffect) {
    const id = this.id()
    if (!id) return
    this.service.revokeUnitUnlock(id, effect.unitType as UnitType, effect.unitLevel).subscribe({
      next: (updated) => {
        this.technologyResource.set(updated)
        this.translate.get('technology.effect.message.removed').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('technology.effect.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
