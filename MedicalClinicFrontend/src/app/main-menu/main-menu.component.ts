import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { AuthenticationService } from '../services/authentication.service';
import { LanguageService } from '../services/language.service';

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.css']
})
export class MainMenuComponent implements OnInit {

  loggedInUser: string;
  translate: Translate;

  constructor(public auth: AuthenticationService, private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
   }

  ngOnInit(): void {
  }

}
