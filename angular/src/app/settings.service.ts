import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, pluck } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  url = 'api/settings/';

  constructor(private $http: HttpClient) { }

  saveSettings(setting: Settings) {

    const list: SettingsInfo[] = [];

    // LOOP ON OBJECT KEYS AND CALL settingSave(name: string, value: string)
    Object.keys(setting).forEach(settingKey => {
      if (setting.hasOwnProperty(settingKey) && setting[settingKey]) {
        list.push({ name: settingKey, value: setting[settingKey] });
      }
    });

    this.saveAll(list);
  }

  private saveAll(settings: SettingsInfo[]) {
    // CALL POST ON API
    this.$http.post(this.url, {settings}).subscribe();
    console.log(settings);
  }

  private getAll(): Observable<SettingsInfo[]> {
    return this.$http.get<{settings: SettingsInfo[]}>(this.url).pipe(
      pluck('settings')
    );
  }

  getSettings(): Observable<Settings> {
    return this.getAll().pipe(
      map(
        (settingsInfoList: SettingsInfo[]) => {
          const settings: Settings = {};

          console.log(settingsInfoList);

          settingsInfoList.forEach(settingInfo => {
            settings[settingInfo.name] = settingInfo.value;
          });

          return settings;
        }
      )
    );
  }

}

export interface SettingsInfo {
  name: string;
  value: string;
}

export interface Settings {
  smartyStreetsAuthID?: string;
  smartyStreetsAPIKey?: string;
  smartyStreetsActivation?: string;

  nistURL?: string;
  nistActivation?: string;
  nistConnectionStatus?: string;
  nistException?: string;
}
