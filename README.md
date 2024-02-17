A simple course registration system in Java that connects to a MySQL database using JDBC. The program allows students to enroll in courses, drop courses, view their transcript, and check what courses they need to complete their degree. This project was done for a university project for which we were given a database designed to imitate a university database. 

The program performs the following:

1)  At startup, prompt the user for their student ID

2) Prompt the user to select one of the operations: Get Transcript, Check Degree Requirements, Add Course, Remove Course, or Exit

3) Based on their input, run the appropriate queries using Dynamic SQL:

	a) Get Transcript: use a student’s ID to query their transcript using Dynamic SQL. 	The transcript should include the course number, course title, semester, year, 	grade, and credits of every course the Student has taken in chronological order.
	
	b) Check Degree Requirements: use a student’s ID to query all of the courses that 	the student still needs to take to complete their degree. That is, determine the 	course numbers and titles of courses the student has not already taken, but are 	required for their major. Assume the required courses for a major are all of the 	courses for their department (e.g., all Physics courses).

	c) Add Course: use a student’s ID and section identifier, try to enroll (via 	INSERT) the student in the specified section. Be sure to verify that the student is 	not already enrolled in the course and meets all of the prerequisites.
	To get the section identifier, prompt the user for the semester and year, and list 	the course_id and sec_id of all matching sections. Number the matching sections (1, 	2, 3, …) so that a user can use the number to specify which section they want to 	enroll in.

	d) Remove Course: using a student’s ID and section identifier, try to remove (via 	DELETE) the specified section from the student’s enrollment. Be sure to verify that 	the student is already enrolled in the course.
	Get the section identifier similar to Add Course. List all sections they are 	enrolled in and prompt the user to specify which they want to remove.
