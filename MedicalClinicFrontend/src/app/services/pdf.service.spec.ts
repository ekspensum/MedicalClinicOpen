import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { AppRoutingModule } from '../app-routing.module';
import { MockUserService } from '../mocks/mock-user.service';
import { MockVisitService } from '../mocks/mock-visit.service';
import { Company } from '../models/company';
import { Diagnosis } from '../models/diagnosis';
import { Doctor } from '../models/doctor';
import { Patient } from '../models/patient';
import { Referral } from '../models/referral';
import { User } from '../models/user';
import { Visit } from '../models/visit';
import { WorkingWeek } from '../models/working-week';
import { PdfService } from './pdf.service';
import { UserService } from './user.service';
import { VisitService } from './visit.service';

describe('PdfService', () => {
  let service: PdfService;
  let company: Company;
  let visit: Visit;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        AppRoutingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [TranslateService,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: VisitService, useClass: MockVisitService },
        { provide: UserService, useClass: MockUserService }
      ]
    });
    service = TestBed.inject(PdfService);
    company = new Company();
    visit = new Visit();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  it('should be created', () => {
    visit.visitDateTime = new Date();
    visit.diagnosis = new Diagnosis();
    visit.patient = new Patient(new User());
    visit.doctor = new Doctor(new User(), new WorkingWeek());
    window['pdfMake'] = {
      createPdf: function (param) {
        return {
          open: function () {
            return true;
          },
          download: function () {
            return true;
          }
        };
      }
    };
    spyOn(window, 'open');
    service.generateDiagnosisPdf(visit);
    expect(service).toBeTruthy();
  });
  it('should be created', () => {
    visit.visitDateTime = new Date();
    visit.diagnosis = new Diagnosis();
    visit.patient = new Patient(new User());
    visit.doctor = new Doctor(new User(), new WorkingWeek());
    window['pdfMake'] = {
      createPdf: function (param) {
        return {
          open: function () {
            return true;
          },
          download: function () {
            return true;
          }
        };
      }
    };
    spyOn(window, 'open');
    service.generateReferralPdf(new Referral(), visit);
    expect(service).toBeTruthy();
  });
  it('should be created', () => {
    visit.visitDateTime = new Date();
    visit.diagnosis = new Diagnosis();
    visit.patient = new Patient(new User());
    visit.doctor = new Doctor(new User(), new WorkingWeek());
    const visitList = new Array<Visit>();
    visitList.push(visit);
    window['pdfMake'] = {
      createPdf: function (param) {
        return {
          open: function () {
            return true;
          },
          download: function () {
            return true;
          }
        };
      }
    };
    spyOn(window, 'open');
    service.generateVisitListPdf(visitList, '2021-01-01', '2021-01-31');
    expect(service).toBeTruthy();
  });
});
