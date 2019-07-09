import csv, json
from datetime import date
import random

csvFilePath = "/Users/cmishra/Desktop/bloom_filter_poc/data/BlackFriday.csv"
jsonFilePath = "/Users/cmishra/Desktop/bloom_filter_poc/data/BlackFriday.txt"

def generate_random_date():
    today_date = date.today()
    start_dt= today_date.replace(year=today_date.year-3).toordinal()
    end_dt = today_date.toordinal()
    random_day = date.fromordinal(random.randint(start_dt, end_dt))
    return str(random_day)

data =[]
with open(csvFilePath) as csvFile:
    csvReader = csv.DictReader(csvFile)
    count =0;
    for rows in csvReader:
        row = {
                "user_id": rows['User_ID'],
                "merchant_id":rows['Product_ID'],
                "total_spend": rows['Purchase'],
                "day": generate_random_date()}
        data.append(str(row))
        #count = count+1
        #if count == 1000:
            #break
with open(jsonFilePath, 'w') as jsonFile:
    for line in data:
        jsonFile.write(line + "\n")
