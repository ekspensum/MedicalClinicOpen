import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Translate } from '../interfaces/translate';
import { ClinicFreeDay } from '../models/clinic-free-day';
import { Company } from '../models/company';
import { Diagnosis } from '../models/diagnosis';
import { Doctor } from '../models/doctor';
import { DoctorFreeDay } from '../models/doctor-free-day';
import { DoctorFreeDayData } from '../models/doctor-free-day-data';
import { Employee } from '../models/employee';
import { MedicalDocument } from '../models/medical-document';
import { Patient } from '../models/patient';
import { Patterns } from '../models/patterns';
import { Referral } from '../models/referral';
import { User } from '../models/user';
import { UserServiceAnswer } from '../models/user-service-answer';
import { Visit } from '../models/visit';
import { VisitData } from '../models/visit-data';
import { WorkingWeek } from '../models/working-week';
import { LanguageService } from '../services/language.service';

@Injectable({
  providedIn: 'root'
})
export class MockVisitService {

  languageService: LanguageService;
  patterns: Patterns;
  translate: Translate;
  answer: UserServiceAnswer;
  visitIdForDetails: number = 0;
  company: Company;
  visit: Visit;
  user: User;
  doctor: Doctor;
  workingWeek: WorkingWeek;
  visitList: Array<Visit>;
  doctorList: Array<Doctor>;
  doctorFreeDay: DoctorFreeDay;
  doctorFreeDayList: Array<DoctorFreeDay>;
  clinicFreeDayList: Array<ClinicFreeDay>;

  constructor() {
    this.languageService = TestBed.inject(LanguageService);
    this.translate = this.languageService.loadLanguage();
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
    this.company = new Company();
    this.visit = new Visit();
    this.user = new User();
    this.workingWeek = new WorkingWeek();
    this.visit.patient = new Patient(this.user);
    this.visit.patient.medicalDocumentList = new Array<MedicalDocument>();
    this.doctor = new Doctor(this.user, this.workingWeek);
    this.visit.doctor = this.doctor;
    this.visit.employee = new Employee(this.user);
    this.visitList = new Array();
    this.doctorList = new Array();
    this.doctorFreeDay = new DoctorFreeDay();
    this.doctorFreeDayList = new Array();
    this.clinicFreeDayList = new Array();
   }

   addClinicFreeDay(clinicFreDay){
    this.clinicFreeDayList.length = 0;
    this.clinicFreeDayList.push(new ClinicFreeDay());
    this.clinicFreeDayList.push(new ClinicFreeDay());
    let clinicData = new HttpResponse<any>({
      "body": this.clinicFreeDayList
    });
    return of(clinicData);
   }
   getClinicFreeDayList(){
    this.clinicFreeDayList.length = 0;
    this.clinicFreeDayList.push(new ClinicFreeDay());
    let clinicData = new HttpResponse<any>({
      "body": this.clinicFreeDayList
    });
    return of(clinicData);
   }
   removeClinicFreeDay(clinicFreeDayId: number){
    let userData = new HttpResponse<any>({
      "body": "FREE_DAY_REMOVED"
    });
    return of(userData);
   }
   addDoctorFreeDayByAdmin(doctorFreeDayData: DoctorFreeDayData){
    this.doctorFreeDay.doctor = new Doctor(new User(), new WorkingWeek());
    this.doctorFreeDayList.push(this.doctorFreeDay);
    this.doctorFreeDayList.push(this.doctorFreeDay);
    let doctorData = new HttpResponse<any>({
      "body": this.doctorFreeDayList
    });
    return of(doctorData);
   }
   getDoctorFreeDayListByAdmin(){
    this.doctorFreeDay.doctor = new Doctor(new User(), new WorkingWeek());
    this.doctorFreeDayList.push(this.doctorFreeDay);
    this.doctorFreeDayList.push(this.doctorFreeDay);
    let doctorData = new HttpResponse<any>({
      "body": this.doctorFreeDayList
    });
    return of(doctorData);
   }
   addDoctorFreeDayByDoctor(doctorFreeDayData: DoctorFreeDayData){
    this.doctorFreeDayList.length = 0;
    this.doctorFreeDay.doctor = new Doctor(new User(), new WorkingWeek());
    this.doctorFreeDayList.push(this.doctorFreeDay);
    this.doctorFreeDayList.push(this.doctorFreeDay);
    let doctorData = new HttpResponse<any>({
      "body": this.doctorFreeDayList
    });
    return of(doctorData);
   }
   getDoctorFreeDayListByDoctor(doctorUsername: string){
    this.doctorFreeDayList.length = 0;
    this.doctorFreeDay.doctor = new Doctor(new User(), new WorkingWeek());
    this.doctorFreeDayList.push(this.doctorFreeDay);
    let doctorData = new HttpResponse<any>({
      "body": this.doctorFreeDayList
    });
    return of(doctorData);
   }
   removeDoctorFreeDay(doctorFreeDayId: number){
    let userData = new HttpResponse<any>({
      "body": "FREE_DAY_REMOVED"
    });
    return of(userData);
   }
   readAllDoctorsForAgendaPage(from: number, to: number){
    this.doctorList.length = 0;
    this.doctor.workingWeek.workingWeekMap = new Array();
    this.doctorList.push(this.doctor);
    let doctorData = new HttpResponse<any>({
      "body": this.doctorList
    });
    return of(doctorData);
   }
   getPlannedVisits(visitData: VisitData){
    this.visitList.length = 0;
    this.visit.visitDateTime = new Date();
    this.visitList.push(this.visit);
    this.visitList.push(this.visit);
    let visits = new HttpResponse<any>({
      "body": this.visitList
    });
    return of(visits);
   }
   getDoctorVisitsByDateAndStatus(visitData: VisitData){
    this.visitList.length = 0;
    this.visit.visitDateTime = new Date();
    this.visitList.push(this.visit);
    this.visitList.push(this.visit);
    this.visitList.push(this.visit);
    let visits = new HttpResponse<any>({
      "body": this.visitList
    });
    return of(visits);
   }
   getDoctorPlannedVisitsByDate(visitData: VisitData){
    this.visitList.length = 0;
    this.visit.visitDateTime = new Date();
    this.visitList.push(this.visit);
    this.visitList.push(this.visit);
    let visits = new HttpResponse<any>({
      "body": this.visitList
    });
    return of(visits);
   }
   scheduleVisitByEmployee(visitData: VisitData){
    let visit = new HttpResponse<any>({
      "body": 'SCHEDULED_VISIT'
    });
    return of(visit);
   }
   confirmationVisit(visitId: number){
    let visit = new HttpResponse<any>({
      "body": 'CONFIRMED_VISIT'
    });
    return of(visit);
   }
   getPatientVisits(visitData: VisitData){
    this.visitList.length = 0;
    let visits = new HttpResponse<any>({
      "body": this.visitList
    });
    return of(visits);
   }
  getVisit(visitId: number) {
    this.visit.diagnosis = new Diagnosis();
    this.visit.diagnosis.referralList = new Array();
    this.visit.diagnosis.referralList.push(new Referral());
    const medicalDocument = new MedicalDocument();
    medicalDocument.enabled = true;
    this.visit.patient.medicalDocumentList.length = 0;
    this.visit.patient.medicalDocumentList.push(medicalDocument);
    let visitData = new HttpResponse<any>({
      "body": this.visit
    });
    return of(visitData);
  }
  deleteVisit(visitId: number){
    let visitData = new HttpResponse<any>({
      "body": 'DELETED_VISIT'
    });
    return of(visitData);
  }
  makeDiagnosis(visit: Visit){
    let visitData = new HttpResponse<any>({
      "body": 'ADDED_DIAGNOSIS'
    });
    return of(visitData);
  }
  removeNotWorkingHours(doctorList: Array<Doctor>): void {
    for(let i = 0; i < doctorList.length; i++){
      for(let j = 0; j < doctorList[i].workingWeek.workingWeekMap.length; j++){
        for(let k = 0; k < doctorList[i].workingWeek.workingWeekMap[j].workingHourMapList.length; k++){
          if(doctorList[i].workingWeek.workingWeekMap[j].workingHourMapList[k].working === false){
            doctorList[i].workingWeek.workingWeekMap[j].workingHourMapList.splice(k, 1);
            k = -1;
          }
        }
      }
    }
   }
  switchVisitStatus(visitStatus: any): string {
    switch (visitStatus) {
      case 'PLANNED':
        return this.translate.visit.statusPlanned;
      case 'CONFIRMED':
        return this.translate.visit.statusConfirmed;
      case 'COMPLETED':
        return this.translate.visit.statusCompleted;
    }
  }
}
