SET SERVEROUTPUT ON SIZE 32000

CREATE OR REPLACE PROCEDURE pro_AvgGrade
AS
  v_answer number(10);
  v_min_avg number:= 100.0;
  v_max_avg number:= 0.0;
  CURSOR school_cursor IS
    SELECT School.SchoolName, AVG(Student.Grade)
      FROM School, Student WHERE School.SchoolId = Student.SchoolId GROUP BY School.SchoolId, School.SchoolName
      ORDER BY School.SchoolName;
  v_schoolname varchar(30);
  v_studentaverage number;
  v_lower_bound number:= 0;
  v_upper_bound number:= 10;
BEGIN
  OPEN school_cursor;
  FETCH school_cursor INTO v_schoolname, v_studentaverage;
  WHILE school_cursor%found loop
    IF v_min_avg > v_studentaverage THEN
      v_min_avg:= v_studentaverage;
    END IF;
    IF v_max_avg < v_studentaverage THEN
      v_max_avg:= v_studentaverage;
    END IF;
    FETCH school_cursor INTO v_schoolname, v_studentaverage;
  end loop;
  CLOSE school_cursor;
  dbms_output.put('SCHOOLNAME' || CHR(9) || 'AVGRADE:' || CHR(9));

  WHILE (v_lower_bound < 100) loop
    IF(v_lower_bound = 0 AND (v_min_avg >= v_lower_bound AND v_min_avg <= v_upper_bound)) THEN
      dbms_output.put('>'||v_lower_bound || ', ' || '<=' ||v_upper_bound||CHR(9));
    ELSIF ((v_min_avg > v_lower_bound AND v_min_avg <= v_upper_bound)OR(v_max_avg > v_lower_bound AND v_max_avg <= v_upper_bound)) THEN
      dbms_output.put('>'||v_lower_bound || ', ' || '<=' ||v_upper_bound||CHR(9));
    ELSIF (v_min_avg < v_lower_bound AND v_max_avg > v_upper_bound) THEN
      dbms_output.put('>'||v_lower_bound || ', ' || '<=' ||v_upper_bound||CHR(9));
    END IF;
    v_lower_bound:=v_lower_bound+10;
    v_upper_bound:=v_upper_bound+10;
  end loop;
  dbms_output.put_line(CHR(10));

  OPEN school_cursor;
  FETCH school_cursor INTO v_schoolname, v_studentaverage;
  WHILE school_cursor%found loop
    dbms_output.put(v_schoolname || CHR(9) || CHR(9)|| CHR(9) || CHR(9) || CHR(9));
    v_lower_bound:= 0;
    v_upper_bound:=10;
    WHILE (v_lower_bound < 100) loop
      IF(v_lower_bound = 0 AND (v_min_avg >= v_lower_bound AND v_min_avg <= v_upper_bound)) THEN
        IF(v_studentaverage >= v_lower_bound AND v_studentaverage <= v_upper_bound) THEN
          dbms_output.put('X');
        ELSE
          dbms_output.put(CHR(9) || CHR(9));
        END IF;
      ELSIF ((v_min_avg > v_lower_bound AND v_min_avg <= v_upper_bound)OR(v_max_avg > v_lower_bound AND v_max_avg <= v_upper_bound)) THEN
        IF(v_studentaverage > v_lower_bound AND v_studentaverage <= v_upper_bound) THEN
          dbms_output.put('X');
        ELSE
          dbms_output.put(CHR(9) || CHR(9));
        END IF;
      ELSIF (v_min_avg < v_lower_bound AND v_max_avg > v_upper_bound) THEN
        IF(v_studentaverage > v_lower_bound AND v_studentaverage <= v_upper_bound) THEN
          dbms_output.put('X');
        ELSE
          dbms_output.put(CHR(9)|| CHR(9));
        END IF;
      END IF;
      v_lower_bound:=v_lower_bound+10;
      v_upper_bound:=v_upper_bound+10;
    end loop;
    FETCH school_cursor INTO v_schoolname, v_studentaverage;
    dbms_output.put_line(CHR(10));
  end loop;
  CLOSE school_cursor;
  dbms_output.put_line(CHR(10));
END pro_AvgGrade;
/

BEGIN
  pro_AvgGrade;
END;
/

CREATE OR REPLACE PROCEDURE pro_DispInternSummary
AS
CURSOR student_internship_cursor IS
  WITH INTERN_COUNT AS
    (SELECT Student.STUDENTID, NVL(COUNT(INTERNSHIP.COMPID),0) AS INTERNSHIP_COUNT FROM STUDENT
      LEFT JOIN INTERNSHIP
      ON INTERNSHIP.STUDENTID = STUDENT.STUDENTID
      GROUP BY Student.STUDENTID
      ORDER BY NVL(COUNT(INTERNSHIP.COMPID),0))
    SELECT INTERN_COUNT.INTERNSHIP_COUNT AS numberofinternships, Count(*) AS numStudents
        FROM INTERN_COUNT GROUP BY INTERN_COUNT.INTERNSHIP_COUNT ORDER BY numberofinternships;
  v_num_of_internships number;
  v_num_of_students number;
  v_min_num_of_internships number := 100000;
  v_max_num_of_internships number := 0;
  v_find_median number:= 0;
  v_is_odd_median number := 0;
  v_loop_count number := 0;
BEGIN
  OPEN student_internship_cursor;

  FETCH student_internship_cursor INTO v_num_of_internships, v_num_of_students;
  WHILE student_internship_cursor%found loop
    IF v_min_num_of_internships > v_num_of_internships THEN
      v_min_num_of_internships := v_num_of_internships;
    END IF;
    IF v_max_num_of_internships < v_num_of_internships THEN
      v_max_num_of_internships := v_num_of_internships;
    END IF;
    FETCH student_internship_cursor INTO v_num_of_internships, v_num_of_students;
  end loop;
  CLOSE student_internship_cursor;

  v_find_median:= (v_max_num_of_internships - v_min_num_of_internships) + 1;
  IF MOD(v_find_median, 2) = 0 THEN
    v_is_odd_median:= 0;
  ELSE
    v_is_odd_median:= 1;
  END IF;
  v_find_median:= ROUND(v_find_median/2);

  OPEN student_internship_cursor;
  FETCH student_internship_cursor INTO v_num_of_internships, v_num_of_students;
  dbms_output.put_line('numberOfInternships' || CHR(9) || '|' || CHR(9) || 'Number of Students');
  v_loop_count:= 1;
  WHILE v_min_num_of_internships <= v_max_num_of_internships loop
    IF v_num_of_internships = v_min_num_of_internships THEN
      dbms_output.put(v_num_of_internships || CHR(9) || CHR(9) || CHR(9) || CHR(9) || CHR(9) || v_num_of_students);
      FETCH student_internship_cursor INTO v_num_of_internships, v_num_of_students;
    ELSE
      dbms_output.put(v_min_num_of_internships || CHR(9) || CHR(9) || CHR(9) || CHR(9) || CHR(9) || '0');
    END IF;

    IF (v_is_odd_median = 1 AND v_loop_count = v_find_median) THEN
      dbms_output.put_line('<---median');
    ELSE
      dbms_output.put_line('');
    END IF;

    v_min_num_of_internships:= v_min_num_of_internships +1;
    v_loop_count:= v_loop_count + 1;
  END loop;
  CLOSE student_internship_cursor;
END pro_DispInternSummary;
/

BEGIN
  pro_DispInternSummary;
END;
/

CREATE OR REPLACE PROCEDURE pro_AddIntern
AS
  CURSOR company_cursor IS Select Company.compid, Company.CompName FROM Company;
  CURSOR recruiter_cursor IS Select Recruiter.RecId, Recruiter.RecName FROM Recruiter;
  CURSOR student_cursor IS Select Student.StudentId, Student.StudentName FROM Student ORDER BY Student.StudentId;
  v_comp_id number;
  v_comp_name varchar(30);
  v_rec_id number;
  v_rec_name varchar(30);
  v_student_id number;
  v_student_name varchar(30);
  is_student_in_table BOOLEAN := FALSE;

  student_name varchar(30);
  company_name varchar(30);
  recruiter_name varchar(30);
  offer_year number;

BEGIN
  company_name:= '&company_name';
  recruiter_name:= '&recruiter_name';
  student_name:= '&student_name';
  offer_year:= '&offer_year';

  OPEN company_cursor;
  FETCH company_cursor INTO v_comp_id, v_comp_name;
  WHILE company_cursor%found loop
    IF v_comp_name = company_name THEN
      EXIT;
    END IF;
    FETCH company_cursor INTO v_comp_id, v_comp_name;
  END LOOP;
  CLOSE company_cursor;

  OPEN recruiter_cursor;
  FETCH recruiter_cursor INTO v_rec_id, v_rec_name;
  WHILE recruiter_cursor%found loop
    IF v_rec_name = recruiter_name THEN
      EXIT;
    END IF;
    FETCH recruiter_cursor INTO v_rec_id, v_rec_name;
  END LOOP;
  CLOSE recruiter_cursor;

  OPEN student_cursor;
  FETCH student_cursor INTO v_student_id, v_student_name;
  WHILE student_cursor%found loop
    IF v_student_name = student_name THEN
      is_student_in_table:= TRUE;
      EXIT;
    ELSE
      is_student_in_table:= FALSE;
    END IF;
    FETCH student_cursor INTO v_student_id, v_student_name;
  END LOOP;

  IF is_student_in_table THEN
    v_student_id:= v_student_id;
  ELSE
    v_student_id:= v_student_id+1;
  END IF;
  CLOSE student_cursor;

  INSERT INTO Internship VALUES (v_student_id, v_comp_id, v_rec_id, offer_year);
END pro_AddIntern;
/

BEGIN
  pro_AddIntern;
END;
/

CREATE OR REPLACE PROCEDURE pro_DispCompany
AS
  CURSOR ALL_DATA IS
    WITH Num_Of_Interns AS
        (SELECT Company.compid, Nvl(Count(Internship.StudentId),0) AS NumOfStudentInterns FROM COMPANY
            LEFT JOIN Internship ON Company.compid = Internship.compid
            GROUP BY Company.compid ORDER BY Company.compid),
       Average_Grades AS
         (SELECT Internship.CompId, ROUND(Avg(Student.Grade),2) As AverageGrade FROM Internship, Student
           WHERE Internship.StudentId = Student.StudentId
           GROUP BY Internship.CompId ORDER BY Internship.CompId)
      SELECT Company.CompId, Company.CompName, Company.Address, Num_Of_Interns.NumOfStudentInterns , NVL(Average_Grades.AverageGrade, 0) AS AverageGrade
      FROM Company
      LEFT JOIN Num_Of_Interns ON Num_Of_Interns.CompId = Company.compid
      LEFT JOIN Average_Grades ON Average_Grades.CompId = Company.compid
      ORDER BY Company.CompName;
   CURSOR INTERN_COUNT_CURSOR IS
      WITH COUNT_DATA AS
        (SELECT Internship.CompId, Student.SchoolId, Count(*) As InternCount
        FROM INTERNSHIP, STUDENT WHERE Internship.StudentId = Student.StudentId
        GROUP BY INTERNSHIP.CompId, Student.SchoolId ORDER BY INTERNSHIP.CompId)
        SELECT COUNT_DATA.CompId, COUNT_DATA.SchoolId, COUNT_DATA.InternCount, School.SchoolName
          FROM COUNT_DATA, SCHOOL WHERE School.SchoolId = COUNT_DATA.SchoolId ORDER BY School.SchoolName;
   CURSOR INTERN_MAX_CURSOR IS
      WITH Intern_Count_Data AS
        (SELECT Internship.CompId, Student.SchoolId, Count(*) As InternCount
        FROM INTERNSHIP, STUDENT
        WHERE Internship.StudentId = Student.StudentId
        GROUP BY INTERNSHIP.CompId, Student.SchoolId)
        SELECT  Intern_Count_Data.CompId, MAX(Intern_Count_Data.INTERNCOUNT) AS MAX_INTERN
        FROM Intern_Count_Data
        GROUP BY Intern_Count_Data.CompId ORDER BY Intern_Count_Data.CompId;

      v_compid number;
      v_compName varchar(30);
      v_address varchar(30);
      v_num_interns number;
      v_average_grade number;
      v_intern_max_compid number;
      v_intern_max number;
      v_intern_count_compid number;
      v_intern_count_schoolid number;
      v_intern_count_interncount number;
      v_intern_count_schoolname varchar(30);
      v_loop_counter number;
BEGIN
  OPEN ALL_DATA;
  dbms_output.put_line('CompanyName'||CHR(9)||CHR(9)||'Address'||CHR(9)||CHR(9)||'NumOfStudentInterns'||CHR(9)||CHR(9)||'School'||CHR(9)||CHR(9)||'AverageGrade');
  FETCH ALL_DATA INTO v_compid, v_compName, v_address, v_num_interns, v_average_grade;
  WHILE ALL_DATA%found loop
    dbms_output.put(v_compName||CHR(9)||CHR(9)||v_address||CHR(9)||CHR(9)||v_num_interns||CHR(9)||CHR(9));
    IF v_num_interns = 0 THEN
      dbms_output.put('---');
    ELSE
      OPEN INTERN_MAX_CURSOR;
      FETCH INTERN_MAX_CURSOR INTO v_intern_max_compid, v_intern_max;
      WHILE INTERN_MAX_CURSOR%found loop
      IF v_intern_max_compid = v_compid THEN
        EXIT;
      END IF;
      FETCH INTERN_MAX_CURSOR INTO v_intern_max_compid, v_intern_max;
      END loop;
      CLOSE INTERN_MAX_CURSOR;

      OPEN INTERN_COUNT_CURSOR;
      FETCH INTERN_COUNT_CURSOR INTO v_intern_count_compid, v_intern_count_schoolid, v_intern_count_interncount, v_intern_count_schoolname;
      v_loop_counter:= 0;
      WHILE INTERN_COUNT_CURSOR%found loop
        IF (v_intern_count_compid = v_compid AND v_intern_count_interncount = v_intern_max) THEN
          IF v_loop_counter = 0 THEN
            dbms_output.put(v_intern_count_schoolname);
            v_loop_counter:= v_loop_counter+1;
          ELSE
            dbms_output.put('/'||v_intern_count_schoolname);
          END IF;
        END IF;
        FETCH INTERN_COUNT_CURSOR INTO v_intern_count_compid, v_intern_count_schoolid, v_intern_count_interncount, v_intern_count_schoolname;
      END loop;
      CLOSE INTERN_COUNT_CURSOR;
    END IF;
    dbms_output.put_line(CHR(9) ||CHR(9)||v_average_grade);
    FETCH ALL_DATA INTO v_compid, v_compName, v_address, v_num_interns, v_average_grade;
  END loop;
  CLOSE ALL_DATA;
END pro_DispCompany;
/

BEGIN
  pro_DispCompany;
END;
/

CREATE OR REPLACE PROCEDURE pro_SearchStudent
AS
  CURSOR STUDENT_INFO_CURSOR IS
      WITH INTERN_COUNTS AS
            (Select Internship.StudentId, Count(*) As NumOfInternships
            FROM Internship GROUP BY Internship.StudentId),
          JOB_COUNTS AS
            (Select JobApplication.StudentId, Count(*) As NumOfJobsApp
              From JobApplication Group BY JobApplication.StudentId)
      SELECT Student.StudentId, Student.StudentName, School.SchoolName, Student.Grade, NVL(INTERN_COUNTS.NumOfInternships,0) AS NumOfInternships, NVL(JOB_COUNTS.NumOfJobsApp,0) As NumOfJobApp
      FROM Student
      LEFT JOIN School ON School.SchoolId = Student.SchoolId
      LEFT JOIN INTERN_COUNTS ON INTERN_COUNTS.StudentId = Student.StudentId
      LEFT JOIN JOB_COUNTS ON JOB_COUNTS.StudentId = Student.StudentId;
    student_id number;
    v_student_id number;
    v_student_name varchar(30);
    v_schoolname varchar(30);
    v_grade number;
    v_number_of_internships number;
    v_number_of_job_app number;
    v_is_found number;
BEGIN
  student_id:= '&student_id';
  OPEN STUDENT_INFO_CURSOR;
  dbms_output.put_line('StudentId'||CHR(9)||'StudentName'||CHR(9)||'SchoolName'||CHR(9)||'Grade'||CHR(9)||'NumOfInternships'||CHR(9)||'NumOfJobApp');
  FETCH STUDENT_INFO_CURSOR INTO v_student_id, v_student_name, v_schoolname, v_grade, v_number_of_internships, v_number_of_job_app;
  WHILE STUDENT_INFO_CURSOR%found loop
    IF v_student_id = student_id THEN
      dbms_output.put_line(v_student_id||CHR(9)||CHR(9)||v_student_name||CHR(9)||CHR(9)||v_schoolname||CHR(9)||CHR(9)||v_grade||CHR(9)||CHR(9)||v_number_of_internships||CHR(9)||CHR(9)||v_number_of_job_app);
      v_is_found:= 1;
      EXIT;
    ELSE
      v_is_found:= -1;
    END IF;
    FETCH STUDENT_INFO_CURSOR INTO v_student_id, v_student_name, v_schoolname, v_grade, v_number_of_internships, v_number_of_job_app;
  END loop;
  CLOSE STUDENT_INFO_CURSOR;
  IF v_is_found = -1 THEN
    dbms_output.put_line('--- STUDENT ID NOT FOUND ---');
  END IF;
END pro_SearchStudent;
/

BEGIN
  pro_SearchStudent;
END;
/

CREATE OR REPLACE PROCEDURE pro_SearchRecruiter
AS
CURSOR ALL_DATA_REC IS
    WITH DATA AS (SELECT Internship.RecId, Internship.CompId, Student.StudentId, Student.Grade
                  FROM Internship, Student WHERE Internship.StudentId = Student.StudentId),
    Averages AS (SELECT DATA.RecId, DATA.CompId, AVG(Data.Grade) As AverageStudentGrade
                  FROM DATA GROUP BY DATA.RecId, DATA.CompId),
    Intern_Counts AS (SELECT Internship.RecId, Internship.CompId, Count(*) AS NumberofInterns
                FROM INTERNSHIP GROUP BY Internship.CompId, Internship.RecId)
  SELECT Recruiter.RecId, Averages.CompId, Company.CompName, NVL(Intern_Counts.NumberofInterns,0) As NumberofInternships, NVL(Averages.AverageStudentGrade,0) As AverageStudentGrade
  FROM Recruiter
  LEFT JOIN Averages ON Recruiter.RecId = Averages.RecId
  LEFT JOIN Intern_Counts ON Recruiter.RecId = Intern_Counts.RecId AND Averages.CompId = Intern_Counts.CompId
  LEFT JOIN Company ON Averages.CompId = Company.CompId
  ORDER BY Recruiter.RecId;
CURSOR FIND_REC_CURSOR IS
  SELECT Recruiter.RecId, Recruiter.RecName FROM RECRUITER;
CURSOR MAX_CURSOR IS
    WITH INTERN_SCHOOL_COUNT AS (SELECT Internship.RECID, Internship.StudentId, Student.SchoolId
            FROM STUDENT, INTERNSHIP WHERE Student.StudentId = Internship.StudentId),
          DATA_2 AS (Select INTERN_SCHOOL_COUNT.RecId, INTERN_SCHOOL_COUNT.SchoolId, Count(*) As InternshipCount
                    FROM INTERN_SCHOOL_COUNT GROUP BY INTERN_SCHOOL_COUNT.RecId, INTERN_SCHOOL_COUNT.SchoolId)
    Select DATA_2.RecId, MAX(DATA_2.InternshipCount) As MaxCount FROM DATA_2
        GROUP BY DATA_2.RecId ORDER BY DATA_2.RecId;
CURSOR COUNTS_CURSOR IS
    WITH INTERN_SCHOOL_COUNT AS (SELECT Internship.RECID, Internship.StudentId, Student.SchoolId
              FROM STUDENT, INTERNSHIP WHERE Student.StudentId = Internship.StudentId),
          DATA_1 AS (Select INTERN_SCHOOL_COUNT.RecId, INTERN_SCHOOL_COUNT.SchoolId, Count(*) As InternshipCount
                FROM INTERN_SCHOOL_COUNT GROUP BY INTERN_SCHOOL_COUNT.RecId, INTERN_SCHOOL_COUNT.SchoolId
                ORDER BY INTERN_SCHOOL_COUNT.RecId)
      SELECT DATA_1.RecId, School.SchoolName, DATA_1.InternshipCount
      FROM DATA_1, SCHOOL WHERE DATA_1.SchoolId = School.SchoolId;

  v_counts_rec_id number;
  v_counts_schoolname varchar(30);
  v_counts_internshipcount number;
  recruiter_name varchar(30);
  v_is_found_rec number;
  v_findrec_rec_id number;
  v_findrec_recname varchar(30);
  v_rec_id number;
  v_compid number;
  v_compname varchar(30);
  v_number_of_internships number;
  v_average_grade number;
  v_maxcursor_recid number;
  v_maxcursor_maxallschool number;
  v_is_max_found number;
  v_school_count number;
BEGIN
  recruiter_name:= '&recruiter_name';
  OPEN FIND_REC_CURSOR;
  FETCH FIND_REC_CURSOR INTO v_findrec_rec_id, v_findrec_recname;
  v_is_found_rec:= -1;
  WHILE FIND_REC_CURSOR%found loop
    IF v_findrec_recname = recruiter_name THEN
      v_is_found_rec:= 1;
      EXIT;
    ELSE
      v_is_found_rec:= -1;
    END IF;
    FETCH FIND_REC_CURSOR INTO v_findrec_rec_id, v_findrec_recname;
  END loop;
  CLOSE FIND_REC_CURSOR;
  IF v_is_found_rec = -1 THEN
    dbms_output.put_line('Given Recruiter Name Not Found.');
  ELSE
    dbms_output.put_line('RecId:' || v_findrec_rec_id);
    dbms_output.put_line('RecName:' || v_findrec_recname);
    dbms_output.put('School with Most Internships:');

    OPEN MAX_CURSOR;
    FETCH MAX_CURSOR INTO v_maxcursor_recid, v_maxcursor_maxallschool;
    WHILE MAX_CURSOR%found loop
      IF v_maxcursor_recid = v_findrec_rec_id THEN
        v_is_max_found:= 1;
        EXIT;
      ELSE
        v_is_max_found:= -1;
      END IF;
      FETCH MAX_CURSOR INTO v_maxcursor_recid, v_maxcursor_maxallschool;
    END loop;
    CLOSE MAX_CURSOR;
    IF v_is_max_found = 1 THEN
      OPEN COUNTS_CURSOR;
        FETCH COUNTS_CURSOR INTO v_counts_rec_id, v_counts_schoolname, v_counts_internshipcount;
        v_school_count:= 0;
        WHILE COUNTS_CURSOR%found loop
          IF (v_counts_rec_id = v_findrec_rec_id AND v_maxcursor_maxallschool = v_counts_internshipcount) THEN
            IF v_school_count = 0 THEN
              v_school_count:= v_school_count+1;
              dbms_output.put(v_counts_schoolname);
            ELSE
              dbms_output.put('/'||v_counts_schoolname);
            END IF;
          END IF;
          FETCH COUNTS_CURSOR INTO v_counts_rec_id, v_counts_schoolname, v_counts_internshipcount;
        END loop;
      CLOSE COUNTS_CURSOR;
    END IF;
    dbms_output.put_line('');
    dbms_output.put_line('CompanyName'||CHR(9)||'NumberOfInternships'||CHR(9)||'AverageStudentGrade');
    OPEN ALL_DATA_REC;
    FETCH ALL_DATA_REC INTO v_rec_id, v_compid, v_compname, v_number_of_internships, v_average_grade;
    WHILE ALL_DATA_REC%found loop
      IF v_rec_id = v_findrec_rec_id THEN
        dbms_output.put_line(v_compname||CHR(9)||CHR(9)||CHR(9)||v_number_of_internships||CHR(9)||CHR(9)||CHR(9)||v_average_grade);
      END IF;
      FETCH ALL_DATA_REC INTO v_rec_id, v_compid, v_compname, v_number_of_internships, v_average_grade;
    END loop;
    CLOSE ALL_DATA_REC;
  END IF;

END pro_SearchRecruiter;
/

BEGIN
  pro_SearchRecruiter;
END;
.
run;
