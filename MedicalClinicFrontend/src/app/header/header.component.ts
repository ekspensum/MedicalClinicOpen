import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { LanguageService } from '../services/language.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  translate: Translate;

  constructor(private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
   }

  ngOnInit(): void {
  }

}
