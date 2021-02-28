import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { LanguageService } from '../services/language.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  translate: Translate;

  constructor(private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
   }

  ngOnInit(): void {
  }

}
