import { DayOfWeek } from 'src/app/enums/day-of-week.enum';
import { WorkingHourMap } from './working-hour-map';

export class WorkingDayMap {

  dayOfWeek: DayOfWeek;
  working: boolean;
  workingHourMapList: Array<WorkingHourMap>;
  visitDate: Date;
  freeDay: boolean;

  constructor() {}

}
