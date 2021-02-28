import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { AppRoutingModule } from '../app-routing.module';

import { AuthGuardEmployeeService } from './auth-guard-employee.service';

describe('AuthGuardEmployeeService', () => {
  let service: AuthGuardEmployeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        AppRoutingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [TranslateService, HttpClient,
        { provide: APP_BASE_HREF, useValue : '/' }
    ]
    });
    service = TestBed.inject(AuthGuardEmployeeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
