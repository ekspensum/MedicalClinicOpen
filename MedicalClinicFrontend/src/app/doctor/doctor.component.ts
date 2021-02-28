import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { LanguageService } from '../services/language.service';

@Component({
  selector: 'app-doctor',
  templateUrl: './doctor.component.html',
  styleUrls: ['./doctor.component.css']
})
export class DoctorComponent implements OnInit {

  translate: Translate;

  constructor(private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
   }

  ngOnInit(): void {
  }

}
