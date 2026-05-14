import {Component} from '@angular/core'
import {RouterModule} from '@angular/router'
import {ShellComponent} from "@board-buddy/ui"

@Component({
  imports: [RouterModule, ShellComponent],
  selector: 'portal-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
