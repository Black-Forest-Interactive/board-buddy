import {Component, computed, inject, signal} from '@angular/core'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatChipsModule} from '@angular/material/chips'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PortalWorkflowService, PortalParticipantInfo} from '@board-buddy/portal'
import {Technology, WorkflowResearchRequest} from '@board-buddy/core'

const TIERS = [1, 2, 3, 4, 5]

@Component({
  selector: 'portal-session-research-dialog',
  imports: [MatDialogModule, MatButtonModule, MatIconModule, MatTooltipModule, MatChipsModule, TranslatePipe],
  templateUrl: './session-research-dialog.component.html',
})
export class SessionResearchDialogComponent {
  private workflowService = inject(PortalWorkflowService)
  private dialogRef = inject(MatDialogRef<SessionResearchDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, participantInfo: PortalParticipantInfo} = inject(MAT_DIALOG_DATA)

  readonly tiers = TIERS

  private discoveredIds = computed(() => new Set(this.data.participantInfo.technologies.map(t => t.id)))

  readonly techsByTier = computed(() =>
    TIERS.reduce((acc, tier) => {
      acc[tier] = this.data.participantInfo.availableTechnologies.filter(t => t.tier === tier)
      return acc
    }, {} as Record<number, Technology[]>)
  )

  readonly researchable = signal<Set<number>>(new Set())

  constructor() {
    const disc = this.discoveredIds()
    const available = this.data.participantInfo.availableTechnologies
    const researchable = new Set<number>()
    for (const tech of available) {
      if (!disc.has(tech.id) && this.canResearch(tech, disc)) researchable.add(tech.id)
    }
    this.researchable.set(researchable)
  }

  private canResearch(tech: Technology, disc: Set<number>): boolean {
    if (tech.tier === 1) return true
    const available = this.data.participantInfo.availableTechnologies
    const prevTierCount = available.filter(t => t.tier === tech.tier - 1 && disc.has(t.id)).length
    const sameTierCount = available.filter(t => t.tier === tech.tier && disc.has(t.id)).length
    return sameTierCount < prevTierCount - 1
  }

  isDiscovered(tech: Technology): boolean { return this.discoveredIds().has(tech.id) }
  isResearchable(tech: Technology): boolean { return this.researchable().has(tech.id) }

  research(tech: Technology) {
    const request = new WorkflowResearchRequest(this.data.participantInfo.player.player.id, tech.id)
    this.workflowService.research(this.data.sessionKey, request).subscribe({
      next: () => {
        this.translate.get('session.message.technologyResearched').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
