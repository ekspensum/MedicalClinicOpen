import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { VisitStatus } from 'src/app/enums/visit-status.enum';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';

import { VisitDetailsForEmployeeComponent } from './visit-details-for-employee.component';

describe('VisitDetailsForEmployeeComponent', () => {
  let component: VisitDetailsForEmployeeComponent;
  let fixture: ComponentFixture<VisitDetailsForEmployeeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitDetailsForEmployeeComponent ],
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
        { provide: VisitService, useClass: MockVisitService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitDetailsForEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on ngOnInit', () => {
    component.ngOnInit();
    expect(component.visit.patient.medicalDocumentList.length).toEqual(1);
  });
  it('shuold be correct visit status', ()=> {
    expect(component.translateVisitStatus(VisitStatus.Planned)).toBe('Planned');
  });
});
