import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { async, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AppRoutingModule } from '../app-routing.module';
import { Doctor } from '../models/doctor';
import { DoctorFreeDayData } from '../models/doctor-free-day-data';
import { Patterns } from '../models/patterns';
import { User } from '../models/user';
import { Visit } from '../models/visit';
import { VisitData } from '../models/visit-data';
import { WorkingDayMap } from '../models/working-day-map';
import { WorkingHourMap } from '../models/working-hour-map';
import { WorkingWeek } from '../models/working-week';
import { VisitService } from './visit.service';

describe('VisitService', () => {
  let visitService: VisitService;
  let httpMock: HttpTestingController;
  const patterns = new Patterns();
  let user: User;
  let workingWeek: WorkingWeek;
  let doctor: Doctor;
  let doctorList: Array<Doctor>;
  let visitData: VisitData;
  let doctorFreeDayData: DoctorFreeDayData;
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
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    });
    visitService = TestBed.inject(VisitService);
    httpMock = TestBed.inject(HttpTestingController);
    user = new User();
    workingWeek = new WorkingWeek();
    doctor = new Doctor(user, workingWeek);
    doctorList = new Array();
    visitData = new VisitData();
    doctorFreeDayData = new DoctorFreeDayData();
    visit = new Visit();
  });

  it('should be created', () => {
    expect(visitService).toBeTruthy();
  });
  it('should be call removeNotWorkingHours and return workingHourMapList length 0', () => {
    workingWeek.workingWeekMap = new Array();

    let workingDayMap1 = new WorkingDayMap();
    let workingHourMap1 = new WorkingHourMap();
    workingHourMap1.working = false;
    workingDayMap1.workingHourMapList = new Array();
    workingDayMap1.workingHourMapList.push(workingHourMap1);
    workingWeek.workingWeekMap.push(workingDayMap1);

    let workingDayMap2 = new WorkingDayMap();
    let workingHourMap2 = new WorkingHourMap();
    workingHourMap2.working = true;
    workingDayMap2.workingHourMapList = new Array();
    workingDayMap2.workingHourMapList.push(workingHourMap2);
    workingWeek.workingWeekMap.push(workingDayMap2);

    doctor.workingWeek = workingWeek;
    doctorList.push(doctor);
    visitService.removeNotWorkingHours(doctorList)
    expect(doctorList[0].workingWeek.workingWeekMap[0].workingHourMapList.length).toEqual(0);
  });
  it('should call scheduleVisitByPatient and return confirmation', async(() => {
    visitService.scheduleVisitByPatient(visitData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/scheduleVisitByPatient');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(visitData);
  }));
  it('should return error if scheduleVisitByPatient ended defeat', async(() => {
    const errorType = 'cannot scheduleVisitByPatient';
    visitService.scheduleVisitByPatient(visitData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/scheduleVisitByPatient');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call scheduleVisitByEmployee and return confirmation', async(() => {
    visitService.scheduleVisitByEmployee(visitData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/scheduleVisitByEmployee');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(visitData);
  }));
  it('should return error if scheduleVisitByEmployee ended defeat', async(() => {
    const errorType = 'cannot scheduleVisitByEmployee';
    visitService.scheduleVisitByEmployee(visitData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/scheduleVisitByEmployee');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call addClinicFreeDay and return clinic free day list', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    const dateNow = new Date();
    visitService.addClinicFreeDay(dateNow).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addClinicFreeDay');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual(dateNow);
  }));
  it('should return error if addClinicFreeDay ended defeat', async(() => {
    const errorType = 'cannot addClinicFreeDay';
    visitService.addClinicFreeDay(new Date()).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addClinicFreeDay');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call get clinic free day list and return it', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.getClinicFreeDayList().subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getClinicFreeDayList');
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if task get clinic free day ended defeat', async(() => {
    const errorType = 'cannot getClinicFreeDayList';
    visitService.getClinicFreeDayList().subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getClinicFreeDayList');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call removeClinicFreeDay and return confirmation', async(() => {
    const clinicFreeDayId = 333;
    visitService.removeClinicFreeDay(clinicFreeDayId).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/removeClinicFreeDay/' + clinicFreeDayId);
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('DELETE');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.url).toEqual(patterns.baseUrl + '/removeClinicFreeDay/' + clinicFreeDayId);
  }));
  it('should return error if removeClinicFreeDay ended defeat', async(() => {
    const clinicFreeDayId = 444;
    const errorType = 'cannot removeClinicFreeDay';
    visitService.removeClinicFreeDay(clinicFreeDayId).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/removeClinicFreeDay/' + clinicFreeDayId);
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call addDoctorFreeDayByAdmin and return clinic free day list', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.addDoctorFreeDayByAdmin(doctorFreeDayData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addDoctorFreeDayByAdmin');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual(doctorFreeDayData);
  }));
  it('should return error if addDoctorFreeDayByAdmin ended defeat', async(() => {
    const errorType = 'cannot addDoctorFreeDayByAdmin';
    visitService.addDoctorFreeDayByAdmin(doctorFreeDayData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addDoctorFreeDayByAdmin');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call addDoctorFreeDayByDoctor and return clinic free day list', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.addDoctorFreeDayByDoctor(doctorFreeDayData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addDoctorFreeDayByDoctor');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual(doctorFreeDayData);
  }));
  it('should return error if addDoctorFreeDayByDoctor ended defeat', async(() => {
    const errorType = 'cannot addDoctorFreeDayByDoctor';
    visitService.addDoctorFreeDayByDoctor(doctorFreeDayData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addDoctorFreeDayByDoctor');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call get doctor free day list by admin and return it', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.getDoctorFreeDayListByAdmin().subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorFreeDayListByAdmin');
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if task doctor free day by admin ended defeat', async(() => {
    const errorType = 'cannot getDoctorFreeDayListByAdmin';
    visitService.getDoctorFreeDayListByAdmin().subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorFreeDayListByAdmin');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call get doctor free day list by doctor and return it', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.getDoctorFreeDayListByDoctor('doctorUsername').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorFreeDayListByDoctor');
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if task doctor free day by doctor ended defeat', async(() => {
    const errorType = 'cannot getDoctorFreeDayListByDoctor';
    visitService.getDoctorFreeDayListByDoctor('doctorUsername').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorFreeDayListByDoctor');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call removeDoctorFreeDay and return confirmation', async(() => {
    const doctorFreeDayId = 333;
    visitService.removeDoctorFreeDay(doctorFreeDayId).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/removeDoctorFreeDay/' + doctorFreeDayId);
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('DELETE');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.url).toEqual(patterns.baseUrl + '/removeDoctorFreeDay/' + doctorFreeDayId);
  }));
  it('should return error if removeDoctorFreeDay ended defeat', async(() => {
    const doctorFreeDayId = 444;
    const errorType = 'cannot removeDoctorFreeDay';
    visitService.removeDoctorFreeDay(doctorFreeDayId).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/removeDoctorFreeDay/' + doctorFreeDayId);
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call get all doctors for agenda page and return it', async(() => {
    const from = 0;
    const to = 7;
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.readAllDoctorsForAgendaPage(from, to).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getAllDoctorsForAgendaPage/' + from + '/' + to);
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if task get all doctors for agenda page ended defeat', async(() => {
    const from = 0;
    const to = 7;
    const errorType = 'cannot readAllDoctorsForAgendaPage';
    visitService.readAllDoctorsForAgendaPage(from, to).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getAllDoctorsForAgendaPage/' + from + '/' + to);
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call getPlannedVisits and return visits planned list', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.getPlannedVisits(visitData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getPlannedVisits');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual(visitData);
  }));
  it('should return error if getPlannedVisits ended defeat', async(() => {
    const errorType = 'cannot getPlannedVisits';
    visitService.getPlannedVisits(visitData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getPlannedVisits');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call deleteVisit and return confirmation', async(() => {
    const visitId = 11;
    visitService.deleteVisit(visitId).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/deleteVisit/' + visitId);
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('DELETE');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.url).toEqual(patterns.baseUrl + '/deleteVisit/' + visitId);
  }));
  it('should return error if deleteVisit ended defeat', async(() => {
    const visitId = 22;
    const errorType = 'cannot deleteVisit';
    visitService.deleteVisit(visitId).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/deleteVisit/' + visitId);
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call get visit and return it', async(() => {
    const visitId = 77;
    const mockTestData = { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' };
    visitService.getVisit(visitId).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getVisit/' + visitId);
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if task get visit ended defeat', async(() => {
    const visitId = 88;
    const errorType = 'cannot getVisit';
    visitService.getVisit(visitId).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getVisit/' + visitId);
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call confirmationVisit and return confirmation', async(() => {
    const visitId = 99;
    visitService.confirmationVisit(visitId).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/confirmationVisit');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PATCH');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(visitId);
  }));
  it('should return error if confirmationVisit ended defeat', async(() => {
    const visitId = 66;
    const errorType = 'cannot confirmationVisit';
    visitService.confirmationVisit(visitId).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/confirmationVisit');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call getDoctorPlannedVisitsByDate and return visits planned list', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.getDoctorPlannedVisitsByDate(visitData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorPlannedVisitsByDate');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual(visitData);
  }));
  it('should return error if getDoctorPlannedVisitsByDate ended defeat', async(() => {
    const errorType = 'cannot getDoctorPlannedVisitsByDate';
    visitService.getDoctorPlannedVisitsByDate(visitData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorPlannedVisitsByDate');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call getDoctorVisitsByDateAndStatus and return visits planned list', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    visitService.getDoctorVisitsByDateAndStatus(visitData).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorVisitsByDateAndStatus');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual(visitData);
  }));
  it('should return error if getDoctorVisitsByDateAndStatus ended defeat', async(() => {
    const errorType = 'cannot getDoctorVisitsByDateAndStatus';
    visitService.getDoctorVisitsByDateAndStatus(visitData).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctorVisitsByDateAndStatus');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call makeDiagnosis and return confirmation', async(() => {
    visitService.makeDiagnosis(visit).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/makeDiagnosis');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(visit);
  }));
  it('should return error if makeDiagnosis ended defeat', async(() => {
    const errorType = 'cannot makeDiagnosis';
    visitService.makeDiagnosis(visit).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/makeDiagnosis');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('method switchVisitStatus should return correct data for PLANNED', () => {
    const answerActual = visitService.switchVisitStatus('PLANNED');
    expect(visitService.translate.visit.statusPlanned).toEqual(answerActual);
  });
  it('method switchVisitStatus should return correct data for CONFIRMED', () => {
    const answerActual = visitService.switchVisitStatus('CONFIRMED');
    expect(visitService.translate.visit.statusConfirmed).toEqual(answerActual);
  });
  it('method switchVisitStatus should return correct data for COMPLETED', () => {
    const answerActual = visitService.switchVisitStatus('COMPLETED');
    expect(visitService.translate.visit.statusCompleted).toEqual(answerActual);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
