import {Component, computed, inject, resource, signal} from '@angular/core'
import {Router} from '@angular/router'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {RuleSetService} from '@board-buddy/admin'
import {RuleSet} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {RuleSetDialogComponent} from './rule-set-dialog/rule-set-dialog.component'

@Component({
  selector: 'admin-rule-set',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './rule-set.component.html',
  styleUrl: './rule-set.component.scss',
})
export class RuleSetComponent {
  private service = inject(RuleSetService)
  private dialog = inject(MatDialog)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private ruleSetResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getRuleSets(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.ruleSetResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.ruleSetResource.value()?.totalSize ?? 0)
  readonly loading = this.ruleSetResource.isLoading
  readonly error = this.ruleSetResource.error
  readonly columns = ['name', 'unitDefinitions', 'actions']

  openDetail(ruleSet: RuleSet) {
    this.router.navigate(['/rule-set', ruleSet.id])
  }

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openCreate() {
    this.dialog.open(RuleSetDialogComponent)
      .afterClosed().subscribe(saved => {if (saved) this.ruleSetResource.reload()})
  }

  openEdit(ruleSet: RuleSet) {
    this.dialog.open(RuleSetDialogComponent, {data: ruleSet})
      .afterClosed().subscribe(saved => {if (saved) this.ruleSetResource.reload()})
  }

  delete(ruleSet: RuleSet) {
    this.service.remove(ruleSet.id).subscribe({
      next: () => {
        this.translate.get('rule-set.message.deleted').subscribe(t => this.toast.success(t))
        this.ruleSetResource.reload()
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
