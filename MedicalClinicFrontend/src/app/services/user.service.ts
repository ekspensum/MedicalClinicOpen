import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Translate } from '../interfaces/translate';
import { Company } from '../models/company';
import { Doctor } from '../models/doctor';
import { Employee } from '../models/employee';
import { MedicalDocument } from '../models/medical-document';
import { Owner } from '../models/owner';
import { Patient } from '../models/patient';
import { Patterns } from '../models/patterns';
import { Role } from '../models/role';
import { UserServiceAnswer } from '../models/user-service-answer';
import { WorkingWeek } from '../models/working-week';
import { LanguageService } from './language.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  patterns: Patterns;
  translate: Translate;
  roleSelect: Role;
  roleEmployee: Role;
  roleAdmin: Role;
  roleList: Array<Role>;
  answer: UserServiceAnswer;

  constructor(private http: HttpClient, private languageService: LanguageService) {
    this.patterns = new Patterns();
    this.translate = languageService.loadLanguage();
    this.answer = new UserServiceAnswer();
  }

  // methods for Patient
  savePatientByRegistrationPage(patient: Patient, captchaResponse: string): Observable<any> {
    const url = this.patterns.baseUrl + '/addPatientByRegistrationPage';
    const headers = new HttpHeaders({
      reCaptcha: captchaResponse
    });
    return this.http.post(url, patient, { headers: headers, responseType: 'text', observe: 'response' });
  }
  getPatient(username: string): Observable<any> {
    const url = this.patterns.baseUrl + '/getPatient';
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.post(url, username, { headers: headers, observe: 'response' });
  }
  selfUpdatePatient(patient: Patient): Observable<any> {
    const url = this.patterns.baseUrl + '/selfUpdatePatient';
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.put(url, patient, { headers: headers, responseType: 'text', observe: 'response' });
  }
  addOrUpdateMedicalDocumentation(medicalDocumentList: Array<MedicalDocument>): Observable<any> {
    const url = this.patterns.baseUrl + '/addOrUpdateMedicalDocumentation';
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.put(url, medicalDocumentList, { headers: headers, responseType: 'text', observe: 'response' });
  }
  removeMedicalDocument(medicalDocumentId: number): Observable<any> {
    const url = this.patterns.baseUrl + '/removeMedicalDocument/' + medicalDocumentId;
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.delete(url, { headers: headers, responseType: 'text', observe: 'response' });
  }
  getMedicalByteFile(medicalDocumentId: number): Observable<any> {
    const url = this.patterns.baseUrl + '/getMedicalByteFile/' + medicalDocumentId;
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.get(url, { headers: headers, responseType: 'blob', observe: 'response' });
  }
   // methods for visit
  readAllDoctorsForScheduleVisit(from: number, to: number): Observable<any> {
    const url = this.patterns.baseUrl + '/getAllDoctorsForScheduleVisit/'+from+'/'+to;
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.get(url, { headers: headers, observe: 'response' });
  }
   removeUserPhoto(username: string): Observable<any> {
    const url = this.patterns.baseUrl + '/removeUserPhoto';
    const token = 'Bearer ' + sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: token
    });
    return this.http.put(url, username, { headers: headers, responseType: 'text', observe: 'response' });
  }
  decodePesel(pesel: string): any {
    const testPesel = new RegExp(this.patterns.pesel);
    let isValidPesel: boolean;
    let gender: string;
    if (!testPesel.test(pesel)) {
      isValidPesel = false;
    } else {
      const weight = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
      let sum = 0;
      const controlNumber = parseInt(pesel.substring(10, 11));
      for (let i = 0; i < weight.length; i++) {
        sum += (parseInt(pesel.substring(i, i + 1)) * weight[i]);
      }
      sum = sum % 10;
      isValidPesel = ((10 - sum) % 10 === controlNumber);
    }
    if (isValidPesel) {
      gender = (parseInt(pesel.substring(9, 10)) % 2 === 1) ? this.translate.user.male : this.translate.user.female;
    } else {
      gender = null;
    }
    return {
      valid: isValidPesel,
      gender: gender
    };
  }
  switchDayOfWeek(dayOfWeek: any): string {
    switch (dayOfWeek) {
      case 'MONDAY':
        return this.translate.home.monday;
      case 'TUESDAY':
        return this.translate.home.tuesday;
      case 'WEDNESDAY':
        return this.translate.home.wednesday;
      case 'THURSDAY':
        return this.translate.home.thursday;
      case 'FRIDAY':
        return this.translate.home.friday;
      case 'SATURDAY':
        return this.translate.home.saturday;
      case 'SUNDAY':
        return this.translate.home.sunday;
    }
  }
  switchAnswer(answer: any): UserServiceAnswer {
    switch (answer) {
      case 'ADDED':
        this.answer.serverAnswer = answer;
        this.answer.visableForm = false;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.user.success_add_msg;
        break;
      case 'NOTUNIQUE':
        this.answer.serverAnswer = answer;
        this.answer.notunique = true;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.not_unique;
        break;
      case 'INVALID':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.invalid;
        break;
      case 'RESET':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.login.success_reset_pass;
        break;
      case 'UPDATED':
        this.answer.serverAnswer = answer;
        this.answer.visableForm = false;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.user.successUpdate;
        break;
      case 'SENT_EMAIL':
        this.answer.serverAnswer = answer;
        this.answer.visableForm = false;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.home.confirmSentMail;
        break;
      case 'RECOVER_PASS_MAIL':
        this.answer.serverAnswer = answer;
        this.answer.visableForm = false;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.login.recoverPassSentEmail;
        break;
      case 0:
        this.answer.errorStatus = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.no_connect;
        break;
      case 400:
        this.answer.errorStatus = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.err_400;
        break;
      case 401:
        this.answer.errorStatus = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.err_401;
        break;
      case 403:
        this.answer.errorStatus = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.err_403;
        break;
      case 404:
        this.answer.errorStatus = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.err_404;
        break;
      case 423:
        this.answer.errorStatus = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.err_423;
        break;
      default:
        if (answer >= 500) {
          this.answer.errorStatus = answer;
          this.answer.subject = this.translate.error.subject;
          this.answer.message = this.translate.error.err_500;
        }
        break;
    }
    return this.answer;
  }
}
