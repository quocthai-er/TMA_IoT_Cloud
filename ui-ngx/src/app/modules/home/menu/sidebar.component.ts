
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MenuService } from '@core/services/menu.service';
import { MenuSection } from '@core/services/menu.models';

@Component({
  selector: 'tb-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./side-menu.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class SidebarComponent implements OnInit {

  menuSections$ = this.menuService.menuSections();

  constructor(private menuService: MenuService) {
  }

  trackByMenuSection(index: number, section: MenuSection){
    return section.id;
  }

  ngOnInit(): void {
  }

}
