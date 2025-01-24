import { Component } from '@angular/core';

@Component({
  selector: 'app-mortgage-calculator',
  templateUrl: './mortgage-calculator.component.html',
  styleUrls: ['./mortgage-calculator.component.css'],
})
export class MortgageCalculatorComponent {
  loanAmount: number = 0;
  annualInterestRate: number = 0;
  loanTermYears: number = 0;
  monthlyPayment: number = 0;
  totalPayment: number = 0;
  totalInterest: number = 0;

  calculateMortgage() {
    const monthlyRate = this.annualInterestRate / 100 / 12;
    const numberOfPayments = this.loanTermYears * 12;

    if (monthlyRate === 0) {
      this.monthlyPayment = this.loanAmount / numberOfPayments;
    } else {
      this.monthlyPayment =
        (this.loanAmount * monthlyRate) /
        (1 - Math.pow(1 + monthlyRate, -numberOfPayments));
    }

    this.totalPayment = this.monthlyPayment * numberOfPayments;
    this.totalInterest = this.totalPayment - this.loanAmount;
  }
}
