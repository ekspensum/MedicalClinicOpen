import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { LanguageService } from '../services/language.service';

@Component({
  selector: 'app-patient',
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.css']
})
export class PatientComponent implements OnInit {

  translate: Translate;

  constructor(private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
   }

  ngOnInit(): void {
  }

}
