INSERT INTO exam (id, name, content, type, updateAt) VALUES (1, 'test1 exam 1', ' ', 'INSPECTION', '2019-02-28T02:20:20');
INSERT INTO exam (id, name, content, type, updateAt) VALUES (2, 'test2 exam 2', ' ', 'INSPECTION', '2019-02-28T02:20:20');
INSERT INTO exam (id, name, content, type, updateAt) VALUES (3, 'test3 exam 3', ' ', 'ISSUE', '2019-02-28T02:20:20');

INSERT INTO plan (id, vehicleId, addressId, day, updateAt) VALUES (1, 1, 1, 11, '2019-02-28T02:20:20');
INSERT INTO plan (id, vehicleId, addressId, day, updateAt) VALUES (2, 2, 1, 11, '2019-02-28T02:20:20');


INSERT INTO vehicle (id, groupId, name, year, make, model, status, type, vin, plate, rental, imgsrc, odometer, createAt, updateAt) VALUES (1, 1, 'car1', '2006' ,'toyota','prius', 'ACTIVE', 'VE', '12345678901234567', 'few-243', 0, '', 1201, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO vehicle (id, groupId, name, year, make, model, status, type, vin, plate, rental, imgsrc, odometer, createAt, updateAt) VALUES (2, 1, 'car2', '2007' ,'toyota','lexus', 'ACTIVE',  'VE', '12345678901234567', 'few-243', 0, '', 1110, '2019-02-28T02:20:20', '2019-02-28T02:20:20');

INSERT INTO person (id, firstName, lastName, phone, email, driverLicense, groupId, type, imgsrc, createAt, updateAt) VALUES (1, 'User', 'A', '1233232342', 'user_a@hiveel.com', '13dsfr422e', 0,  'DR', '', '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO person (id, firstName, lastName, phone, email, driverLicense, groupId, type, imgsrc, createAt, updateAt) VALUES (2, 'User', 'B', '1233232342', 'user_b@hiveel.com', '13dsfr422e', 2, 'VE', '', '2019-02-28T02:20:21', '2019-02-28T02:20:21');
INSERT INTO person (id, firstName, lastName, phone, email, driverLicense, groupId, type, imgsrc, createAt, updateAt) VALUES (3, 'User', 'C', '1233232342', 'user_c@hiveel.com', '13dsfr422e', 3,  'AS', '', '2019-02-28T02:20:22', '2019-02-28T02:20:22');

INSERT INTO personGroup (id, type, name, dashboard, inspection, issues, exam, vehicle, person, invoice, setting, notification, updateAt) VALUES (2, 'AS', 'AutoSav', 'EDIT', 'EDIT', 'EDIT', 'EDIT', 'VIEW', 'EDIT', 'VIEW', 'EDIT', 'VIEW', '2019-02-28T02:20:20');
INSERT INTO personGroup (id, type, name, dashboard, inspection, issues, exam, vehicle, person, invoice, setting, notification, updateAt) VALUES (3, 'VE', 'CarManager', 'EDIT', 'VIEW', 'VIEW', 'VIEW', 'EDIT', 'EDIT', 'VIEW', 'EDIT', 'VIEW', '2019-02-28T02:20:20');
INSERT INTO personGroup (id, type, name, dashboard, inspection, issues, exam, vehicle, person, invoice, setting, notification, updateAt) VALUES (4, 'VE', 'SystemManager', 'EDIT', 'EDIT', 'EDIT', 'EDIT', 'EDIT', 'EDIT', 'EDIT', 'EDIT', 'EDIT', '2019-02-28T02:20:20');

INSERT INTO vehicleGroup (id, name, content, updateAt) VALUES (1, 'Group 1', 'North California', '2019-02-28T02:20:20');
INSERT INTO vehicleGroup (id, name, content, updateAt) VALUES (2, 'Group 2', 'South California', '2019-02-28T02:20:20');
INSERT INTO vehicleGroup (id, name, content, updateAt) VALUES (3, 'Group 3', 'Arizona', '2019-02-28T02:20:20');

INSERT INTO inspection (id, vehicleId, driverId, autosaveId, addressId, date, odometer, name, content, status, tax, createAt, updateAt) VALUES (1, 1, 1, 3, 1, '2019-03-04 17:00:01', 1323, 'test inspect 1', ' ', 'QUOTED', 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO inspection (id, vehicleId, driverId, autosaveId, addressId, date, odometer, name, content, status, tax, createAt, updateAt) VALUES (2, 3, 2, 3, 1, '2019-03-04 17:00:01', 1323, 'test inspect 2', ' ', 'OVERDUE', 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO inspection (id, vehicleId, driverId, autosaveId, addressId, date, odometer, name, content, status, tax, createAt, updateAt) VALUES (3, 1, 2, 3, 1, '2019-04-19T00:00:00', 1323, 'test inspect 2', ' ', 'CONFIRM', 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO inspection (id, vehicleId, driverId, autosaveId, addressId, date, odometer, name, content, status, tax, createAt, updateAt) VALUES (4, 2, 1, 3, 1, '2019-04-20T00:00:00', 1323, 'test inspect 2', ' ', 'COMPLETE', 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');

INSERT INTO issue (id, vehicleId, driverId, name, content, apptMinDate, apptMaxDate, status, odometer, lon, lat, tax, createAt, updateAt) VALUES (1, 2, 2, 'Flat tire', ' ', '2019-03-05T13:00:01', '2019-03-06T13:00:01', 'QUOTED', 1234, 117.9390, 34.0686, 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO issue (id, vehicleId, driverId, name, content, apptMinDate, apptMaxDate, status, odometer, lon, lat, tax, createAt, updateAt) VALUES (2, 2, 1, 'Flat tire', ' ', '2019-03-05T13:00:01', '2019-03-06T13:00:01', 'QUOTED', 1234, 117.9390, 34.0686, 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO issue (id, vehicleId, driverId, name, content, apptMinDate, apptMaxDate, status, odometer, lon, lat, tax, createAt, updateAt) VALUES (3, 1, 2, 'Flat tire', ' ', '2019-03-05T13:00:01', '2019-03-06T13:00:01', 'QUOTED', 1234, 117.9390, 34.0686, 0.1, '2019-02-28T02:20:20', '2019-02-28T02:20:20');

INSERT INTO quote (id, problemId, name, labor, part, updateAt) VALUES (1, 1, 'Replace item', 100, 50, '2019-02-28T02:20:20');
INSERT INTO quote (id, problemId, name, labor, part, updateAt) VALUES (2, 2, 'Buy a item', 10, 200, '2019-02-28T02:20:20');
INSERT INTO quote (id, problemId, name, labor, part, updateAt) VALUES (3, 1, 'Replace item2', 200, 50, '2019-02-28T02:20:20');
INSERT INTO quote (id, problemId, name, labor, part, updateAt) VALUES (4, 2, 'Buy a item2', 10, 200, '2019-02-28T02:20:20');
INSERT INTO quote (id, problemId, name, labor, part, updateAt) VALUES (5, 3, 'Buy a item2', 10, 200, '2019-02-28T02:20:20');

INSERT INTO problem (id, relateId, type, vehicleId, remark, examId, imgsrc1, imgsrc2, imgsrc3, imgsrc4, updateAt) VALUES (1, 1, 'INSPECTION', 1, ' ', 1, ' ', ' ', ' ', ' ', '2019-02-28T02:20:20');
INSERT INTO problem (id, relateId, type, vehicleId, remark, examId, imgsrc1, imgsrc2, imgsrc3, imgsrc4, updateAt) VALUES (2, 1, 'ISSUE', 2, ' ', 3, ' ', ' ', ' ', ' ', '2019-02-28T02:20:20');
INSERT INTO problem (id, relateId, type, vehicleId, remark, examId, imgsrc1, imgsrc2, imgsrc3, imgsrc4, updateAt) VALUES (3, 2, 'INSPECTION', 1, ' ', 1, ' ', ' ', ' ', ' ', '2019-02-28T02:20:20');

INSERT INTO odometer (id, relateId, type, vehicleId, mi, date, updateAt) VALUES (1, 1, 'INSPECTION', 1, 1000, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO odometer (id, relateId, type, vehicleId, mi, date, updateAt) VALUES (2, 1, 'ISSUE', 1, 2000, '2019-02-28T02:20:20', '2019-02-28T02:20:20');
INSERT INTO odometer (id, relateId, type, vehicleId, mi, date, updateAt) VALUES (3, 2, 'INSPECTION', 2, 3000, '2019-02-28T02:20:20', '2019-02-28T02:20:20');

INSERT INTO vehicleDriverRelate (id, vehicleId, driverId, onDate, offDate, onOdometer, offOdometer, updateAt) VALUES (1, 1, 1, '2019-03-04T15:00:00', '', '123', '', '2019-03-04T23:00:00');
INSERT INTO vehicleDriverRelate (id, vehicleId, driverId, onDate, offDate, onOdometer, offOdometer, updateAt) VALUES (2, 2, 1, '2019-03-05T15:00:00', '', '456', '', '2019-03-04T23:00:00');

INSERT INTO login (id, personId, date, ip, updateAt) VALUES (1, 1, '2019-03-04T12:34:21', '140.115.61.93', '2019-03-04T23:00:00');
INSERT INTO login (id, personId, date, ip, updateAt) VALUES (2, 1, '2019-03-05T12:34:21', '140.115.61.93', '2019-03-04T23:00:00');

INSERT INTO reminder (id, content, vehicleId, inspectionId, type, createAt, updateAt, date) VALUES (1, '1-day reminder for the monthly inspection', 1, 1,'INSPECTION_1DAY', '2019-03-04T17:00:01', '2019-03-04T17:00:01','2019-03-04');
INSERT INTO reminder (id, content, vehicleId, inspectionId, type, createAt, updateAt, date) VALUES (2, '1-day reminder for the monthly inspection', 2, 4,'INSPECTION_1DAY', '2019-03-04T17:00:01', '2019-03-04T17:00:01','2019-03-04');

INSERT INTO invoicePlatform (id, price, date, updateAt) VALUES (1, 10, '2019-03-06T12:32:00', '2019-03-04T23:00:00');

INSERT INTO invoiceExam (id, price, date, updateAt) VALUES (1, 100, '2019-03-04T03:23:11', '2019-03-04T23:00:00');

INSERT INTO address (id, name, content, updateAt) VALUES (1, 'location 1', '1050 Lakes Dr. Suite 201, West Covina, CA', '2019-02-28T02:20:20');
INSERT INTO address (id, name, content, updateAt) VALUES (2, 'location 2', '1050 Lakes Dr. Suite 201, West Covina, CA', '2019-02-28T02:20:20');

INSERT INTO tutorial (id, name, filesrc, type, updateAt) VALUES (1, 'tutorial 1', 'https://www.youtube.com/watch?v=dOSzCHmP1xM', 'AS', '2019-02-28T02:20:20');
INSERT INTO tutorial (id, name, filesrc, type, updateAt) VALUES (2, 'tutorial 2', 'https://www.youtube.com/watch?v=PlW67bq1wcM', 'VE', '2019-02-28T02:20:20');


insert into pushRecord (id, type, unread, personId ,  status, createAt, updateAt) VALUES (1, 'PLAN_THREE_DAYS', 1, 1, 'SUCCESS', '2019-02-28T02:20:20', '2019-02-28T02:20:20');
