import {Component, computed, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute} from '@angular/router'
import {map} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameUnit, UnitDefinition, WorkflowCreateUnitRequest} from '@board-buddy/core'
import {PlayerService, PortalWorkflowService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'portal-session-army',
  imports: [MatButtonModule, MatIconModule, MatCardModule, TranslatePipe],
  templateUrl: './session-army.component.html',
})
export class SessionArmyComponent {
  private workflowService = inject(PortalWorkflowService)
  private playerService = inject(PlayerService)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private sessionKey = toSignal(this.route.paramMap.pipe(map(p => p.get('key') ?? '')))
  readonly playerId = computed(() => this.playerService.getPlayerId())

  private workflowResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getWorkflow(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private myInfoResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getMyInfo(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private readonly unitOrder: Record<string, number> = {INFANTRY: 0, MOUNTED: 1, ARTILLERY: 2, AIRCRAFT: 3}
  readonly unitDefinitions = computed(() => {
    const defs = this.workflowResource.value()?.ruleSet.unitDefinitions ?? []
    return [...defs].sort((a, b) => (this.unitOrder[a.unitType] ?? 99) - (this.unitOrder[b.unitType] ?? 99))
  })
  readonly myUnits = computed(() => this.myInfoResource.value()?.units ?? [])
  readonly unitLevel = computed(() => this.myInfoResource.value()?.unitLevel ?? {})

  createUnit(unitDef: UnitDefinition) {
    const key = this.sessionKey()
    const pid = this.playerId()
    if (!key || !pid) return
    this.workflowService.createUnit(key, new WorkflowCreateUnitRequest(pid, unitDef.id)).subscribe({
      next: () => {
        this.translate.get('session.message.unitCreated').subscribe(t => this.toast.success(t))
        this.myInfoResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  unitsOfType(unitType: string): GameUnit[] {
    return this.myUnits().filter(u => u.type?.kind === unitType)
  }

  unitImagePath(kind: string | null | undefined): string | null {
    if (!kind) return null
    const map: Record<string, string> = {INFANTRY: '/img/infantry2.jpg', MOUNTED: '/img/cavalry2.jpg', ARTILLERY: '/img/artillery2.jpg', AIRCRAFT: '/img/plane2.jpg'}
    return map[kind] ?? null
  }

  range(n: number | null | undefined): number[] {
    return Array.from({length: n ?? 0}, (_, i) => i)
  }
}
