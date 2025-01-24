import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'indianCurrencyFormat'
})
export class IndianCurrencyFormatPipe implements PipeTransform {

  transform(value: any): string {
    if (value == null || value === '') {
      return '';  // Return empty if no value
    }

    // Ensure the value is a valid number
    const numberValue = parseFloat(value);
    if (isNaN(numberValue)) {
      console.error('Invalid number value:', value); // Debugging line
      return value; // If it's not a valid number, return original
    }

    // Round to 2 decimal places
    const roundedValue = numberValue.toFixed(2);

    // Split into integer and decimal parts
    const [integerPart, decimalPart] = roundedValue.split('.');

    // Format the integer part for Indian numbering system
    const formattedInteger = this.formatIndianNumber(integerPart);

    // Combine the integer and decimal parts with ₹ symbol
    return `₹ ${formattedInteger}.${decimalPart}`;
  }

  private formatIndianNumber(value: string): string {
    let result = '';
    let count = 0;

    // Start from the right side, adding commas every 3 digits
    for (let i = value.length - 1; i >= 0; i--) {
      result = value[i] + result;
      count++;

      // After the first 3 digits, add a comma after every 2 digits
      if (count === 3 && i !== 0) {
        result = ',' + result;
        count = 0;
      }
    }

    return result;
  }
}
