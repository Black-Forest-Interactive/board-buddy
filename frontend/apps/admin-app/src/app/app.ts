import {Component} from '@angular/core'
import {ShellComponent} from '@board-buddy/ui'

@Component({
  imports: [ShellComponent],
  selector: 'admin-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
