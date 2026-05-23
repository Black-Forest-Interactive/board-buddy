import {Signal} from "@angular/core";

export interface ShellMenuGroup {
  title: string,
  items: ShellMenuItem[]
}

export interface ShellMenuItem {
  routerLink: string,
  icon: string,
  text: string,
  exact?: boolean,
  badges?: ShellMenuBadge[]
}

export interface ShellMenuBadge {
  value: Signal<number>
  colorClass: string
}


