import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Translate } from '../interfaces/translate';
import { Company } from '../models/company';
import { Doctor } from '../models/doctor';
import { Employee } from '../models/employee';
import { MedicalDocument } from '../models/medical-document';
import { Owner } from '../models/owner';
import { Patient } from '../models/patient';
import { Patterns } from '../models/patterns';
import { Role } from '../models/role';
import { User } from '../models/user';
import { UserServiceAnswer } from '../models/user-service-answer';
import { WorkingWeek } from '../models/working-week';
import { LanguageService } from '../services/language.service';

@Injectable({
  providedIn: 'root'
})
export class MockUserService {

  patterns: Patterns;
  translate: Translate;
  answer: UserServiceAnswer;
  company: Company;
  user: User;
  owner: Owner;
  employee: Employee;
  patient: Patient;
  doctor: Doctor;
  workingWeek: WorkingWeek;
  doctorList: Array<Doctor>;
  employeeList: Array<Employee>;
  patientList: Array<Patient>;
  languageService: LanguageService;
  roleSelect: Role;
  roleEmployee: Role;
  roleAdmin: Role;
  roleList: Array<Role>;

  constructor() {
    this.languageService = TestBed.inject(LanguageService);
    this.translate = this.languageService.loadLanguage();
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
    this.company = new Company();
    this.roleSelect = new Role(1, 'Select', this.translate.user.roleSelect);
    this.roleEmployee = new Role(4, 'ROLE_EMPLOYEE', this.translate.user.roleEmployee);
    this.roleAdmin = new Role(5, 'ROLE_ADMIN', this.translate.user.roleAdmin);
    this.roleList = new Array<Role>();
    this.roleList.push(this.roleSelect);
    this.roleList.push(this.roleEmployee);
    this.roleList.push(this.roleAdmin);
    this.user = new User(this.roleList);
    this.owner = new Owner(this.user, this.company);
    this.employee = new Employee(this.user);
    this.patient = new Patient(this.user);
    this.patient.medicalDocumentList = new Array<MedicalDocument>();
    this.workingWeek = new WorkingWeek();
    this.doctor = new Doctor(this.user, this.workingWeek);
    this.doctorList = new Array<Doctor>();
    this.employeeList = new Array<Employee>();
    this.patientList = new Array<Patient>();
  }

  saveAdmin(admin: Employee) {
    let userData = new HttpResponse<any>({
      "body": "ADDED"
    });
    return of(userData);
  }
  updateAdmin(admin: Employee) {
    this.employee.user.enabled = false;
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  getOwner(username: string) {
    this.owner.user.username = 'owner username';
    let userData = new HttpResponse<any>({
      "body": this.owner
    });
    return of(userData);
  }
  selfUpdateOwner(owner: Owner) {
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  saveEmployee(employee: Employee) {
    let userData = new HttpResponse<any>({
      "body": "ADDED"
    });
    return of(userData);
  }
  getEmployee(username: string) {
    this.employee.user.username = 'employee username';
    let userData = new HttpResponse<any>({
      "body": this.employee
    });
    return of(userData);
  }
  updateEmployee(employee: Employee) {
    this.employee.user.enabled = false;
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  selfUpdateEmployee(employee: Employee) {
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  savePatientByAdmin(patient: Patient) {
    let userData = new HttpResponse<any>({
      "body": "ADDED"
    });
    return of(userData);
  }
  savePatientByRegistrationPage(patient: Patient) {
    let userData = new HttpResponse<any>({
      "body": "ADDED"
    });
    return of(userData);
  }
  saveSocialUser(patient: Patient, reCaptchaResponse: string) {
    let userData = new HttpResponse<any>({
      "body": "ADDED"
    });
    return of(userData);
  }
  getPatient(username: string) {
    this.patient.user.username = 'patient username';
    const medicalDocument = new MedicalDocument();
    medicalDocument.registerDateTime = new Date();
    medicalDocument.enabled = true;
    this.patient.medicalDocumentList.length = 0;
    this.patient.medicalDocumentList.push(medicalDocument);
    let userData = new HttpResponse<any>({
      "body": this.patient
    });
    return of(userData);
  }
  findPatient(patientData: string) {
    this.patientList.push(this.patient);
    let userData = new HttpResponse<any>({
      "body": this.patientList
    });
    return of(userData);
  }
  updatePatient(patient: Patient) {
    this.patient.user.enabled = false;
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  selfUpdatePatient(patient: Patient) {
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  getMedicalByteFile(medicalDocumentId: number){
    let userData = new HttpResponse<any>({
      "body": "UPDATE_MEDICAL_DOCUMENTATION"
    });
    return of(userData);
  }
  removeMedicalDocument(medicalDocumentId: number){
    let userData = new HttpResponse<any>({
      "body": "UPDATE_MEDICAL_DOCUMENTATION"
    });
    return of(userData);
  }
  addOrUpdateMedicalDocumentation(medicalDocumentList: Array<MedicalDocument>){
    let userData = new HttpResponse<any>({
      "body": "UPDATE_MEDICAL_DOCUMENTATION"
    });
    return of(userData);
  }
  removePatient(id: number) {
    this.patient.user.enabled = false;
    let userData = new HttpResponse<any>({
      "body": "REMOVED"
    });
    return of(userData);
  }
  orderRemoveMyPatientAccount(username: string) {
    let userData = new HttpResponse<any>({
      "body": "ORDERED_REMOVE_PATIENT"
    });
    return of(userData);
  }
  cancelOrderRemoveMyPatientAccount(username: string) {
    let userData = new HttpResponse<any>({
      "body": "CANCELED_OREDER_REMOVE_PATIENT"
    });
    return of(userData);
  }
  saveDoctor(doctor: Doctor) {
    let userData = new HttpResponse<any>({
      "body": "ADDED"
    });
    return of(userData);
  }
  getDoctor(username: string) {
    this.doctor.user.username = 'doctor username';
    let userData = new HttpResponse<any>({
      "body": this.doctor
    });
    return of(userData);
  }
  updateDoctor(doctor: Doctor) {
    this.doctor.user.enabled = false;
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  setDoctorWorkingWeek(workingWeek: WorkingWeek) {
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  selfUpdateDoctor(doctor: Doctor) {
    let userData = new HttpResponse<any>({
      "body": "UPDATED"
    });
    return of(userData);
  }
  getDoctorWorkingWeek(username: string) {
    let userData = new HttpResponse<any>({
      "body": this.workingWeek
    });
    return of(userData);
  }
  readAllDoctors() {
    this.doctorList.length = 0;
    this.doctor.user.lastName = 'LastName';
    this.doctor.user.enabled = false;
    this.doctor.user.photo = 'some image';
    this.doctorList.push(this.doctor);
    let userData = new HttpResponse<any>({
      "body": this.doctorList
    });
    return of(userData);
  }
  readAllDoctorsForScheduleVisit(from: number, to: number) {
    this.doctorList.length = 0;
    this.doctor.workingWeek.workingWeekMap = new Array();
    this.doctorList.push(this.doctor);
    let userData = new HttpResponse<any>({
      "body": this.doctorList
    });
    return of(userData);
  }
  readAllEmployees() {
    this.employee.user.lastName = 'LastName';
    this.employee.user.enabled = false;
    this.employee.user.photo = 'some image';
    this.employeeList.push(this.employee);
    let userData = new HttpResponse<any>({
      "body": this.employeeList
    });
    return of(userData);
  }
  getCompanyData() {
    let userData = new HttpResponse<any>({
      "body": this.company
    });
    return of(userData);
  }
  resetPasswordByLink(resetPasswordString: string) {
    let userData = new HttpResponse<any>({
      "body": "RESET"
    });
    return of(userData);
  }
  resetPasswordByAdminPage(id: number) {
    let userData = new HttpResponse<any>({
      "body": "RESET"
    });
    return of(userData);
  }
  recoverPassword(username: string, email: string, reCaptchaResponse: string) {
    let userData = new HttpResponse<any>({
      "body": "RECOVER_PASS_MAIL"
    });
    return of(userData);
  }
  activationPatient(activationString: string) {
    let userData = new HttpResponse<any>({
      "body": "ACTIVATED"
    });
    return of(userData);
  }
  decodePesel(pesel: string): any {
    let testPesel = new RegExp(this.patterns.pesel);
    let isValidPesel: boolean;
    let gender: string;
    if (!testPesel.test(pesel)) {
      isValidPesel = false;
    } else {
      let weight = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
      let sum = 0;
      let controlNumber = parseInt(pesel.substring(10, 11));
      for (let i = 0; i < weight.length; i++) {
        sum += (parseInt(pesel.substring(i, i + 1)) * weight[i]);
      }
      sum = sum % 10;
      isValidPesel = ((10 - sum) % 10 === controlNumber);
    }
    if (isValidPesel) {
      gender = (parseInt(pesel.substring(9, 10)) % 2 == 1) ? this.translate.user.male : this.translate.user.female;
    } else {
      gender = null;
    }
    return {
      valid: isValidPesel,
      gender: gender
    }
  }
  findRoleById(id: number) {
    this.roleList = this.getRoleListForEmployee();
    for (let index = 0; index < this.roleList.length; index++) {
      const element = this.roleList[index];
      if (element.id === id) {
        return element;
      }
    }
  }
  getRoleListForEmployee() {
    this.roleSelect = new Role(1, 'Select', this.translate.user.roleSelect);
    this.roleEmployee = new Role(4, 'ROLE_EMPLOYEE', this.translate.user.roleEmployee);
    this.roleAdmin = new Role(5, 'ROLE_ADMIN', this.translate.user.roleAdmin);
    this.roleList = new Array<Role>();
    this.roleList.push(this.roleSelect);
    this.roleList.push(this.roleEmployee);
    this.roleList.push(this.roleAdmin);
    return this.roleList;
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
  switchAnswer(answer: any) {
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
      case 'UNKNOWN':
        this.answer.serverAnswer = answer;
        this.answer.visableForm = false;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.recoverPassUserUnknown;
        break;
      case 'ACTIVATED':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.home.successActivationPatient;
        break;
      case 'ACTIVATED_FAIL':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.error.activationPatient;
        break;
      case 'DELAYED':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.recoverPassDeleyed;
        break;
      case 'REMOVED':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.user.successRemove;
        break;
      case 'ORDERED_REMOVE_PATIENT':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.user.orderRemovePatient;
        break;
      case 'ORDER_REMOVE_PATIENT_DEFEAT':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.orderRemovePatient;
        break;
      case 'ORDER_REMOVE_PATIENT_EXIST':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.orderRemovePatientExist;
        break;
      case 'CANCELED_OREDER_REMOVE_PATIENT':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.user.cancelOrderRemovePatient;
        break;
      case 'CANCEL_OREDER_REMOVE_PATIENT_DEFEAT':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.cancelOrderRemovePatient;
        break;
      case 'CANCEL_OREDER_REMOVE_PATIENT_MAIL_FAILD':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.cancelOrderRemovePatientMail;
        break;
      case 'ORDER_REMOVE_PATIENT_NOT_EXIST':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.orderRemovePatientNotExist;
        break;
      case 'RECAPTCHA_FAIL':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.reCaptcha;
        break;
      case 'PATIEN_NOT_ENABLED':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.patientNotEnabled;
        break;
      case 'PATIEN_NOT_ENABLED_ADMIN':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.patientNotEnabledAdmin;
        break;
      case 'PATIEN_ACCOUNT_EXIST':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.patientAccountExist;
        break;
      case 'PATIEN_ACCOUNT_EXIST_ADMIN':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.patientAccountExistAdmin;
        break;
      case 'PAST_DATE':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.pastDate;
        break;
      case 'FREE_DAY_REMOVED':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.visit.freeDayRemoved;
        break;
      case 'ADDED_DIAGNOSIS':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.visit.makedDiagnosis;
        break;
      case 'DELETED_VISIT':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.visit.deleted;
        break;
      case 'VISIT_TERM_EMPTY':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.error.subject;
        this.answer.message = this.translate.error.visitDateTimeEmpty;
        break;
      case 'CONFIRMED_VISIT':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.visit.confirmed;
        break;
      case 'UPDATE_MEDICAL_DOCUMENTATION':
        this.answer.serverAnswer = answer;
        this.answer.subject = this.translate.user.successSubject;
        this.answer.message = this.translate.visit.updateMedicalDocum;
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
