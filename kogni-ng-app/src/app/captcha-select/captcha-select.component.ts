import { Component } from '@angular/core';
import {MatCheckbox} from "@angular/material/checkbox";
import {ImageModel} from "./image-model";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-captcha-select',
  standalone: true,
  imports: [
    MatCheckbox,
    CommonModule
  ],
  templateUrl: './captcha-select.component.html',
  styleUrl: './captcha-select.component.scss',
})
export class CaptchaSelectComponent {

  public images: ImageModel[] = [new ImageModel(), new ImageModel(), new ImageModel(), new ImageModel(), new ImageModel(),
    new ImageModel(), new ImageModel(), new ImageModel(), new ImageModel()];

}
