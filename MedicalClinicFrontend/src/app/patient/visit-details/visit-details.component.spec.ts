import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { VisitStatus } from 'src/app/enums/visit-status.enum';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';

import { VisitDetailsComponent } from './visit-details.component';

describe('VisitDetailsComponent', () => {
  let component: VisitDetailsComponent;
  let fixture: ComponentFixture<VisitDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitDetailsComponent ],
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
    fixture = TestBed.createComponent(VisitDetailsComponent);
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
    expect(component.translateVisitStatus(VisitStatus.Completed)).toBe('Completed');
  });
});
