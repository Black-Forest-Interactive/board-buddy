import {Component} from '@angular/core';
import {RouterModule} from '@angular/router';
import {Card} from "primeng/card";
import {ButtonDirective} from "primeng/button";

@Component({
  imports: [RouterModule, Card, ButtonDirective],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected title = 'board-buddy';

  sayHello() {
    alert('Hello from Board Buddy!');
  }

  resolveCombat() {

  }
}
