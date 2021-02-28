import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';

import { VisitConfirmationComponent } from './visit-confirmation.component';

describe('VisitConfirmationComponent', () => {
  let component: VisitConfirmationComponent;
  let fixture: ComponentFixture<VisitConfirmationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitConfirmationComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
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
    fixture = TestBed.createComponent(VisitConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('visit list length should be 2', ()=>{
    component.searchPlannedVisits();
    expect(component.visitList.length).toEqual(2);
  });
  it('shuold be correct state on scheduleVisit', ()=>{
    component.confirmVisitTerm();
    expect(component.answer.serverAnswer).toEqual('CONFIRMED_VISIT');
  });
  it('shuold be correct state on onCorrectScheduled', ()=>{
    component.onConfirmedVisit();
    expect(component.answer.serverAnswer ).toBeNull();
    expect(component.visitList.length).toEqual(2);
  });
});
