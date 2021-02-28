import { VisitStatus } from '../enums/visit-status.enum';

export class VisitData {


  constructor(private visitDate?: string, private visitTime?: string, private doctorUsername?: string,
              private patientUsername?: string, private employeeUsername?: string, private patientAilmentsInfo?: string,
              private visitDateFrom?: string, private visitDateTo?: string, private visitStatus?: VisitStatus) { }
}
