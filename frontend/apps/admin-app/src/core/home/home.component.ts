import {Component} from '@angular/core'
import {RouterLink} from '@angular/router'
import {TranslatePipe} from '@ngx-translate/core'
import {MatCardModule} from '@angular/material/card'
import {MatIconModule} from '@angular/material/icon'
import {MatButtonModule} from '@angular/material/button'
import {MainContentComponent} from '@board-buddy/ui'

@Component({
  selector: 'admin-home',
  imports: [MainContentComponent, RouterLink, TranslatePipe, MatCardModule, MatIconModule, MatButtonModule],
  templateUrl: './home.component.html',
})
export class HomeComponent {}
