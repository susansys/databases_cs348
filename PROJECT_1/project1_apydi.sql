------Query 1
WITH RESULT AS (SELECT School.SchoolId,  AVG(Student.Grade) AS AverageGrade FROM SCHOOL, STUDENT
        WHERE School.SchoolId = Student.SchoolId AND Student.Grade >= 60
        GROUP BY School.SchoolId)
SELECT School.SchoolName, TO_CHAR(NVL(Result.AverageGrade,0), '00.00') AS AverageGrade FROM School
        LEFT JOIN Result ON School.SchoolId = Result.SchoolId
        ORDER BY NVL(Result.AverageGrade,0) DESC;

------Query 2
WITH OFFER_COUNTS AS (SELECT Internship.StudentId, Count(*) AS NumOfInternships
FROM INTERNSHIP GROUP BY Internship.StudentId)
    SELECT STUDENT.StudentId, STUDENT.StudentName, NVL(OFFER_COUNTS.NumOfInternships, 0) AS NumOfInternships
          FROM STUDENT, OFFER_COUNTS
          WHERE STUDENT.StudentId = OFFER_COUNTS.StudentId
                AND STUDENT.BirthDate IN (Select MIN(BirthDate) from STUDENT)
                ORDER BY StudentName;

------Query 3
WITH JOB_DATA AS (SELECT STUDENT.StudentId, Count(*) AS NumOfCompanies FROM STUDENT, JobApplication
          WHERE STUDENT.StudentId = JobApplication.StudentId
          GROUP BY STUDENT.StudentId)
  SELECT Student.StudentId, Student.StudentName, NVL(JOB_DATA.NumOfCompanies,0) AS NumOfCompanies FROM STUDENT
          LEFT JOIN JOB_DATA ON JOB_DATA.StudentId = STUDENT.StudentId
          ORDER BY StudentName;

------Query 4
WITH VALID_JOBS AS (SELECT CompID, COUNT(*) AS NumberOfJobs FROM JOB
        WHERE OfferYear = 2014 OR OfferYear = 2015 OR OfferYear = 2016
        GROUP BY CompID)
        SELECT Company.CompName, VALID_JOBS.NumberOfJobs
                FROM VALID_JOBS, Company  WHERE Company.CompID = VALID_JOBS.CompID
                                            AND VALID_JOBS.NumberOfJobs
                                            IN (SELECT MAX(VALID_JOBS.NumberOfJobs) from VALID_JOBS)
                ORDER BY CompName;

------Query 5
WITH VALID_JOBS AS (SELECT CompID, COUNT(*) AS NumberOfJobs FROM JOB
        WHERE OfferYear = 2014 OR OfferYear = 2015 OR OfferYear = 2016
        GROUP BY CompID
        ORDER BY COUNT(*) DESC)
        SELECT Company.CompName, VALID_JOBS.NumberOfJobs
                FROM VALID_JOBS, Company  WHERE Company.CompID = VALID_JOBS.CompID AND ROWNUM <= 3
                ORDER BY CompName;

------Query 6
WITH JOB_COUNT AS (SELECT JOB.CompID, Count(*) AS NumberOfJobs FROM JOB GROUP BY JOB.CompID),
     INTERNSHIP_COUNT AS (SELECT Internship.CompID, Count(*) AS NumberOfInternships FROM Internship GROUP BY Internship.CompID)
    SELECT Company.CompName, NVL(JOB_COUNT.NumberOfJobs,0) AS NumOfJobs, NVL(INTERNSHIP_COUNT.NumberOfInternships,0) AS NumOfInternships FROM Company
        LEFT JOIN JOB_COUNT ON Company.CompID = JOB_COUNT.CompID
        LEFT JOIN INTERNSHIP_COUNT ON Company.CompID = INTERNSHIP_COUNT.CompID
        ORDER BY NVL(JOB_COUNT.NumberOfJobs,0);

------Query 7
WITH INTERNSHIP_COUNT AS (SELECT Internship.StudentID, Count(*) AS NumberOfInternships FROM Internship
                          GROUP BY Internship.StudentID),
     PURDUE_STUDENTS AS (SELECT Student.StudentId, Student.StudentName, student.BirthDate FROM Student, School
                          WHERE STUDENT.SchoolId = School.SchoolId AND UPPER(School.SchoolName)='PURDUE')
     SELECT PURDUE_STUDENTS.StudentId, PURDUE_STUDENTS.StudentName, FLOOR((SYSDATE - PURDUE_STUDENTS.birthdate)/365.25) AS AGE,
            NVL(INTERNSHIP_COUNT.NumberOfInternships,0) AS NumOfInternships FROM PURDUE_STUDENTS
            LEFT JOIN INTERNSHIP_COUNT ON INTERNSHIP_COUNT.StudentID = PURDUE_STUDENTS.StudentID
            ORDER BY PURDUE_STUDENTS.StudentName;

------Query 8
WITH REC_DATA AS (SELECT RECID, COUNT(DISTINCT COMPID) AS NumOfCompanies FROM INTERNSHIP GROUP BY RECID)
      SELECT RECRUITER.RECID, RECRUITER.RECNAME, REC_DATA.NumOfCompanies FROM RECRUITER, REC_DATA
      WHERE REC_DATA.RECID = RECRUITER.RECID AND REC_DATA.NumOfCompanies >= 2;

------Query 9
WITH NUM_DATA AS (SELECT JOBAPPLICATION.JOBID, COUNT(DISTINCT STUDENTID) AS NUMOFSTUDENTS FROM JOBAPPLICATION GROUP BY JOBID),
     JOB_DATA AS (SELECT JOB.CompId, JOB.JOBNUM, JOB.JOBTITLE, JOB.SALARY, NVL(NUM_DATA.NUMOFSTUDENTS, 0) AS NUMOFSTUDENTS FROM JOB
      LEFT JOIN NUM_DATA ON NUM_DATA.JOBID = Job.JobID WHERE JOB.OFFERYEAR=2017)
      SELECT JOB_DATA.JobNum, JOB_DATA.JOBTITLE, JOB_DATA.SALARY, COMPANY.CompName, JOB_DATA.NUMOFSTUDENTS
              FROM JOB_DATA, COMPANY WHERE COMPANY.CompId = JOB_DATA.CompId
              ORDER BY JOB_DATA.JOBNUM, JOB_DATA.JOBTITLE;

------Query 10
WITH FAIL_DATA AS (select SCHOOLID, COUNT(DISTINCT STUDENTID) AS FAIL_COUNT FROM STUDENT
      WHERE STUDENT.GRADE < 60 GROUP BY SCHOOLID)
SELECT SCHOOL.SchoolName, NVL(FAIL_DATA.FAIL_COUNT,0) AS FAILEDCOUNT FROM SCHOOL
    LEFT JOIN FAIL_DATA ON FAIL_DATA.SCHOOLID = SCHOOL.SCHOOLID
    ORDER BY SCHOOL.SchoolName;
