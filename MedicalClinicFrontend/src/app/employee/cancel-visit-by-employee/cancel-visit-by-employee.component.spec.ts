import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { CancelVisitByEmployeeComponent } from './cancel-visit-by-employee.component';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';

describe('CancelVisitByEmployeeComponent', () => {
  let component: CancelVisitByEmployeeComponent;
  let fixture: ComponentFixture<CancelVisitByEmployeeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CancelVisitByEmployeeComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: VisitService, useClass: MockVisitService },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelVisitByEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('patient list length should be 1', () => {
    component.searchPatient();
    expect(component.patientList.length).toEqual(1);
  });
  it('shuold be correct state on selectPatient', ()=>{
    component.searchPatient();
    component.selectedIndexPatient = 0;
    component.selectPatient();
    expect(component.patientList.length).toEqual(0);
  });
  it('shuold be correct state on cancelVisit', ()=>{
    spyOn(window, 'confirm').and.returnValue(true);
    component.cancelVisit();
    expect(component.answer.serverAnswer).toEqual('DELETED_VISIT');
  });
  it('shuold be correct state on onCanceledVisit', ()=>{
    component.onCanceledVisit();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.patient).toBeNull();
  });
});
