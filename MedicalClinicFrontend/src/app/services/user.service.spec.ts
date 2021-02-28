import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule, HttpEvent, HttpEventType, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { async, inject, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { AppRoutingModule } from '../app-routing.module';
import { UserService } from './user.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Patterns } from '../models/patterns';
import { Patient } from '../models/patient';
import { User } from '../models/user';
import { Role } from '../models/role';
import { Owner } from '../models/owner';
import { Company } from '../models/company';
import { Employee } from '../models/employee';
import { WorkingWeek } from '../models/working-week';
import { Doctor } from '../models/doctor';

describe('UserService', () => {
  let userService: UserService;
  let httpMock: HttpTestingController;
  const patterns = new Patterns();
  let user;
  let patient;
  let company;
  let owner;
  let employee;
  let workingWeek;
  let doctor;

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
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    });
    userService = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    user = new User();
    patient = new Patient(user);
    company = new Company();
    owner = new Owner(user, company);
    employee = new Employee(user);
    workingWeek = new WorkingWeek();
    doctor = new Doctor(user, workingWeek);
  });

  it('should be created', () => {
    expect(userService).toBeTruthy();
  });

  // methods for Patient
  it('should call savePatientByRegistrationPage and return it', async(() => {
    patient.id = 12345;
    userService.savePatientByRegistrationPage(patient, 'captchaResponse').subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(12345);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addPatientByRegistrationPage');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: patient });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(patient);
  }));
  it('should return error if savePatientByRegistrationPage ended defeat', async(() => {
    const errorType = 'cannot savePatientByRegistrationPage';
    userService.savePatientByRegistrationPage(patient, 'captchaResponse').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addPatientByRegistrationPage');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call activationPatient and return confirmation', async(() => {
    userService.activationPatient('activationString').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('activationString');
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/activationPatient');
    const expectedResponse = new HttpResponse({ status: 200, body: 'activationString' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual('activationString');
  }));
  it('should return error if activationPatient ended defeat', async(() => {
    const errorType = 'cannot activationPatient';
    userService.activationPatient('activationString').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/activationPatient');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call getPatient and return it', async(() => {
    userService.getPatient('patient').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(patient);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getPatient');
    const expectedResponse = new HttpResponse({ status: 200, body: patient });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual('patient');
  }));
  it('should return error if getPatient ended defeat', async(() => {
    const errorType = 'cannot getPatient';
    userService.getPatient('patient').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getPatient');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call selfUpdatePatient and return confirmation', async(() => {
    patient.id = 12345;
    userService.selfUpdatePatient(patient).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdatePatient');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(patient);
  }));
  it('should return error if selfUpdatePatient ended defeat', async(() => {
    const errorType = 'cannot selfUpdatePatient';
    userService.selfUpdatePatient(patient).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdatePatient');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call orderRemoveMyPatientAccount and return confirmation', async(() => {
    userService.orderRemoveMyPatientAccount('patient').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/orderRemoveMyPatientAccount');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual('patient');
  }));
  it('should return error if orderRemoveMyPatientAccount ended defeat', async(() => {
    const errorType = 'cannot orderRemoveMyPatientAccount';
    userService.orderRemoveMyPatientAccount(patient).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/orderRemoveMyPatientAccount');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call cancelOrderRemoveMyPatientAccount and return confirmation', async(() => {
    userService.cancelOrderRemoveMyPatientAccount('patient').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/cancelOrderRemoveMyPatientAccount');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual('patient');
  }));
  it('should return error if cancelOrderRemoveMyPatientAccount ended defeat', async(() => {
    const errorType = 'cannot cancelOrderRemoveMyPatientAccount';
    userService.cancelOrderRemoveMyPatientAccount(patient).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/cancelOrderRemoveMyPatientAccount');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call saveSocialUser and return it', async(() => {
    patient.id = 54321;
    userService.saveSocialUser(patient, 'captchaResponse').subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(54321);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addSocialUser');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: patient });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(patient);
  }));
  it('should return error if saveSocialUser ended defeat', async(() => {
    const errorType = 'cannot saveSocialUser';
    userService.saveSocialUser(patient, 'captchaResponse').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addSocialUser');
    mockReq.error(new ErrorEvent(errorType));
  }));

  // methods for Owner
  it('should return properly role', () => {
    expect(userService.findRoleById(4)).toEqual(new Role(4, 'ROLE_EMPLOYEE', userService.translate.user.roleEmployee));
  });
  it('should return properly length of role list', () => {
    expect(userService.getRoleListForEmployee().length).toEqual(3);
  });
  it('should call get company data', async(() => {
    const mockTestData = { name: 'Test data', description: 'Lorem ipsum dolor sit amet.' };
    userService.getCompanyData().subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getCompanyData');
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if task getCompanyData ended defeat', async(() => {
    const errorType = 'cannot getCompanyData';
    userService.getCompanyData().subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getCompanyData');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call getOwner and return it', async(() => {
    owner.id = 6789;
    userService.getOwner('owner').subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(6789);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getOwner');
    const expectedResponse = new HttpResponse({ status: 200, body: owner });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual('owner');
  }));
  it('should return error if getOwner ended defeat', async(() => {
    const errorType = 'cannot getOwner';
    userService.getOwner('owner').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getOwner');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call saveAdmin and return it', async(() => {
    employee.id = 101112;
    userService.saveAdmin(employee).subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(101112);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addAdmin');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: employee });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(employee);
  }));
  it('should return error if saveAdmin ended defeat', async(() => {
    const errorType = 'cannot saveAdmin';
    userService.saveAdmin(employee).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addAdmin');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call updateAdmin and return confirmation', async(() => {
    employee.id = 12345;
    userService.updateAdmin(employee).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updateAdmin');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(employee);
  }));
  it('should return error if updateAdmin ended defeat', async(() => {
    const errorType = 'cannot updateAdmin';
    userService.updateAdmin(employee).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updateAdmin');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call selfUpdateOwner and return confirmation', async(() => {
    owner.id = 12345;
    userService.selfUpdateOwner(owner).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdateOwner');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(owner);
  }));
  it('should return error if selfUpdateOwner ended defeat', async(() => {
    const errorType = 'cannot selfUpdateOwner';
    userService.selfUpdateOwner(owner).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdateOwner');
    mockReq.error(new ErrorEvent(errorType));
  }));

  // methods for Owner and Admin
  it('should call readAllEmployees', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    userService.readAllEmployees().subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getAllEmployees');
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if readAllEmployees defeat', async(() => {
    const errorType = 'cannot readAllEmployees';
    userService.readAllEmployees().subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getAllEmployees');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call resetPasswordByAdminPage and return confirmation', async(() => {
    userService.resetPasswordByAdminPage(33).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/resetPasswordByAdminPage');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(33);
  }));
  it('should return error if resetPasswordByAdminPage ended defeat', async(() => {
    const errorType = 'cannot resetPasswordByAdminPage';
    userService.resetPasswordByAdminPage(33).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/resetPasswordByAdminPage');
    mockReq.error(new ErrorEvent(errorType));
  }));

  // methods for Admin
  it('should call saveEmployee and return it', async(() => {
    employee.id = 131415;
    userService.saveEmployee(employee).subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(131415);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addEmployee');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: employee });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(employee);
  }));
  it('should return error if saveEmployee ended defeat', async(() => {
    const errorType = 'cannot saveEmployee';
    userService.saveEmployee(employee).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addEmployee');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call updateEmployee and return confirmation', async(() => {
    employee.id = 12345;
    userService.updateEmployee(employee).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updateEmployee');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(employee);
  }));
  it('should return error if updateEmployee ended defeat', async(() => {
    const errorType = 'cannot updateEmployee';
    userService.updateEmployee(employee).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updateEmployee');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call savePatientByAdmin and return it', async(() => {
    patient.id = 161718;
    userService.savePatientByAdmin(patient).subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(161718);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addPatientByAdminOrEmployee');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: patient });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(patient);
  }));
  it('should return error if savePatientByAdmin ended defeat', async(() => {
    const errorType = 'cannot savePatientByAdmin';
    userService.savePatientByAdmin(employee).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addPatientByAdminOrEmployee');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call findPatient', async(() => {
    const mockTestData = { name: 'Test data', description: 'Lorem ipsum dolor sit amet.' };
    const paramForPatient = 'parameterForSearchPatient';
    userService.findPatient(paramForPatient).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/searchPatient?searchString=' + paramForPatient);
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.params.get('searchString')).toEqual(paramForPatient);
  }));
  it('should return error if task findPatient ended defeat', async(() => {
    const errorType = 'cannot findPatient';
    userService.findPatient('searchString').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/searchPatient?searchString=searchString');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call updatePatient and return confirmation', async(() => {
    patient.id = 202122;
    userService.updatePatient(patient).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updatePatient');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(patient);
  }));
  it('should return error if updatePatient ended defeat', async(() => {
    const errorType = 'cannot updatePatient';
    userService.updatePatient(patient).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updatePatient');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call removePatient and return confirmation', async(() => {
    const patientId = 333;
    userService.removePatient(patientId).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/removePatient/' + patientId);
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('DELETE');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.url).toEqual(patterns.baseUrl + '/removePatient/' + patientId);
  }));
  it('should return error if removePatient ended defeat', async(() => {
    const patientId = 444;
    const errorType = 'cannot removePatient';
    userService.removePatient(patientId).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/removePatient/' + patientId);
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call saveDoctor and return it', async(() => {
    doctor.id = 252627;
    userService.saveDoctor(doctor).subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(252627);
      expect(data.status).toEqual(201);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addDoctor');
    const expectedResponse = new HttpResponse({ status: 201, statusText: 'Created', body: doctor });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(doctor);
  }));
  it('should return error if addDoctor ended defeat', async(() => {
    const errorType = 'cannot addDoctor';
    userService.saveDoctor(doctor).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/addDoctor');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call readAllDoctors', async(() => {
    const mockTestData = [
      { name: 'Test data1', description: 'Lorem ipsum dolor sit amet 1.' },
      { name: 'Test data2', description: 'Lorem ipsum dolor sit amet 2.' }
    ];
    userService.readAllDoctors().subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual(mockTestData);
      expect(data.body.length).toEqual(2);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getAllDoctors');
    const expectedResponse = new HttpResponse({ status: 200, body: mockTestData });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('GET');
    expect(mockReq.request.responseType).toEqual('json');
  }));
  it('should return error if readAllDoctors defeat', async(() => {
    const errorType = 'cannot readAllDoctors';
    userService.readAllDoctors().subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getAllDoctors');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call updateDoctor and return confirmation', async(() => {
    doctor.id = 232425;
    userService.updateDoctor(doctor).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updateDoctor');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(doctor);
  }));
  it('should return error if updateDoctor ended defeat', async(() => {
    const errorType = 'cannot updateDoctor';
    userService.updateDoctor(patient).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/updateDoctor');
    mockReq.error(new ErrorEvent(errorType));
  }));

  // methods for Employee
  it('should call getEmployee and return it', async(() => {
    employee.id = 272829;
    userService.getEmployee('employee').subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(272829);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getEmployee');
    const expectedResponse = new HttpResponse({ status: 200, body: employee });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual('employee');
  }));
  it('should return error if getEmployee ended defeat', async(() => {
    const errorType = 'cannot getEmployee';
    userService.getEmployee('employee').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getEmployee');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call selfUpdateEmployee and return confirmation', async(() => {
    employee.id = 303132;
    userService.selfUpdateEmployee(employee).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdateEmployee');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(employee);
  }));
  it('should return error if selfUpdateEmployee ended defeat', async(() => {
    const errorType = 'cannot selfUpdateEmployee';
    userService.selfUpdateEmployee(employee).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdateEmployee');
    mockReq.error(new ErrorEvent(errorType));
  }));

  // method for Doctor
  it('should call getDoctor and return it', async(() => {
    doctor.id = 333435;
    userService.getDoctor('doctor').subscribe((data: HttpResponse<any>) => {
      expect(data.body.id).toEqual(333435);
      expect(data.status).toEqual(200);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctor');
    const expectedResponse = new HttpResponse({ status: 200, body: doctor });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('json');
    expect(mockReq.request.body).toEqual('doctor');
  }));
  it('should return error if getDoctor ended defeat', async(() => {
    const errorType = 'cannot getDoctor';
    userService.getDoctor('doctor').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/getDoctor');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call selfUpdateDoctor and return confirmation', async(() => {
    doctor.id = 363738;
    userService.selfUpdateDoctor(doctor).subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdateDoctor');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(doctor);
  }));
  it('should return error if selfUpdateDoctor ended defeat', async(() => {
    const errorType = 'cannot selfUpdateDoctor';
    userService.selfUpdateDoctor(doctor).subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/selfUpdateDoctor');
    mockReq.error(new ErrorEvent(errorType));
  }));

  // methods for others
  it('should call recoverPassword and return confirmation', async(() => {
    userService.recoverPassword('login', 'email', 'captchaResponse').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/forgetPassword');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual({ login: 'login', email: 'email' });
  }));
  it('should return error if recoverPassword ended defeat', async(() => {
    const errorType = 'cannot recoverPassword';
    userService.recoverPassword('login', 'email', 'captchaResponse').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/forgetPassword');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('should call resetPasswordByLink and return confirmation', async(() => {
    userService.resetPasswordByLink('resetPasswordString').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/resetPasswordByLink');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('PUT');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual('resetPasswordString');
  }));
  it('should return error if resetPasswordByLink ended defeat', async(() => {
    const errorType = 'cannot resetPasswordByLink';
    userService.resetPasswordByLink('resetPasswordString').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/resetPasswordByLink');
    mockReq.error(new ErrorEvent(errorType));
  }));
  it('method decodePesel should be return valid and female gender', () => {
    const translate: TranslateService = TestBed.inject(TranslateService);
    const peselResult = userService.decodePesel('93120792388');
    expect(peselResult.valid).toBeTruthy();
    if (translate.getBrowserLang() === 'en') {
      expect(peselResult.gender).toEqual('Female');
    } else {
      expect(peselResult.gender).toEqual('Kobieta');
    }
  });
  it('method decodePesel should return invalid and gender null', () => {
    const peselResult = userService.decodePesel('93120792389');
    expect(peselResult.valid).toBeFalsy();
    expect(peselResult.gender).toBeNull();
  });
  it('method switchAnswer should return correct data for ADDED', () => {
    const answerActual = userService.switchAnswer('ADDED');
    expect(userService.answer.serverAnswer).toEqual(answerActual.serverAnswer);
    expect(userService.answer.visableForm).toBeFalsy();
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for NOTUNIQUE', () => {
    const answerActual = userService.switchAnswer('NOTUNIQUE');
    expect(userService.answer.serverAnswer).toEqual(answerActual.serverAnswer);
    expect(userService.answer.notunique).toBeTruthy();
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for RESET', () => {
    const answerActual = userService.switchAnswer('RESET');
    expect(userService.answer.serverAnswer).toEqual(answerActual.serverAnswer);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for UPDATED', () => {
    const answerActual = userService.switchAnswer('UPDATED');
    expect(userService.answer.serverAnswer).toEqual(answerActual.serverAnswer);
    expect(userService.answer.visableForm).toBeFalsy();
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for SENT_EMAIL', () => {
    const answerActual = userService.switchAnswer('SENT_EMAIL');
    expect(userService.answer.serverAnswer).toEqual(answerActual.serverAnswer);
    expect(userService.answer.visableForm).toBeFalsy();
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for ACTIVATED', () => {
    const answerActual = userService.switchAnswer('ACTIVATED');
    expect(userService.answer.serverAnswer).toEqual(answerActual.serverAnswer);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for 0', () => {
    const answerActual = userService.switchAnswer(0);
    expect(userService.answer.errorStatus).toEqual(0);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for 401', () => {
    const answerActual = userService.switchAnswer(401);
    expect(userService.answer.errorStatus).toEqual(401);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for 403', () => {
    const answerActual = userService.switchAnswer(403);
    expect(userService.answer.errorStatus).toEqual(403);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for 404', () => {
    const answerActual = userService.switchAnswer(404);
    expect(userService.answer.errorStatus).toEqual(404);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });
  it('method switchAnswer should return correct data for 500', () => {
    const answerActual = userService.switchAnswer(500);
    expect(userService.answer.errorStatus).toEqual(500);
    expect(userService.answer.subject).toEqual(answerActual.subject);
    expect(userService.answer.message).toEqual(answerActual.message);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
