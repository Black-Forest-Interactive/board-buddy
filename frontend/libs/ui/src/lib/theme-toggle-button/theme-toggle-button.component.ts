import {Component, effect, signal} from '@angular/core';
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'lib-theme-toggle-button',
  imports: [MatIconModule, MatButtonModule, MatTooltipModule],
  templateUrl: './theme-toggle-button.component.html',
  styleUrl: './theme-toggle-button.component.scss',
})
export class ThemeToggleButtonComponent {
  isDarkMode = signal<boolean>(false);

  constructor() {
    // Load saved theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode.set(true);
      document.documentElement.classList.add('dark-theme');
    }

    // Watch for theme changes
    effect(() => {
      if (this.isDarkMode()) {
        document.documentElement.classList.add('dark-theme');
        localStorage.setItem('theme', 'dark');
      } else {
        document.documentElement.classList.remove('dark-theme');
        localStorage.setItem('theme', 'light');
      }
    });
  }

  toggleTheme() {
    this.isDarkMode.update(current => !current);
  }
}
