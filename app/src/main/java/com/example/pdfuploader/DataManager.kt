package com.example.pdfuploader

object DataManager {

    var courses: ArrayList<Course> = ArrayList()

    private var units: ArrayList<CourseUnit> = ArrayList()

    init {
        initializeCourseAndUnits()
    }

    private fun initializeCourseAndUnits() {

        // BIT
        units.add(CourseUnit("Introduction to business studies", "", ""))
        units.add(CourseUnit("Principles of Marketing", "PrinciplesOfMarketing.pdf", ""))
        units.add(CourseUnit("Computer Architecture", "", ""))
        units.add(CourseUnit("Intro to Programming and Algorithms", "IntrotoProgramming.pdf", ""))
        units.add(CourseUnit("Intro to Computer Applications and Packages", "", ""))
        units.add(CourseUnit("Foundation Maths", "", ""))
        units.add(CourseUnit("Communication Skills", "", ""))
        units.add(CourseUnit("Financial Accounting 1", "", ""))
        units.add(CourseUnit("Basic Electricity and optics", "", ""))
        units.add(CourseUnit("System Analysis and Design", "", ""))
        units.add(CourseUnit("Database systems","", ""))
        units.add(CourseUnit("Basic Discrete Maths","", ""))
        units.add(CourseUnit("Fundamentals Of Internet","", ""))
        units.add(CourseUnit("Structured Programming","C++.pdf", ""))
        units.add(CourseUnit("Principles Of Management","",""))
        units.add(CourseUnit("Digital Electronics and Logic","",""))
        units.add(CourseUnit("Object Oriented Programming 1","", ""))
        units.add(CourseUnit("Introduction to Web Design","", ""))
        units.add(CourseUnit("Probability and statics 1","", ""))
        units.add(CourseUnit("Hardware and software Installation","", ""))
        units.add(CourseUnit("Operating System and Design","", ""))
        units.add(CourseUnit("Object Oriented System and Design","", ""))
        units.add(CourseUnit("Data Structures and Algorithms","", ""))
        units.add(CourseUnit("Probability and statistics 2","", ""))
        units.add(CourseUnit("Data Communication and Networks","", ""))
        units.add(CourseUnit("E-Commerce","", ""))
        units.add(CourseUnit("Object Oriented Programming 2","", ""))
        units.add(CourseUnit("Organization Behaviour","", ""))
        units.add(CourseUnit("Software Engineering","", ""))
        units.add(CourseUnit("Event Driven Programming","", ""))
        units.add(CourseUnit("Management Information Systems","", ""))
        units.add(CourseUnit("Development Studies","", ""))
        units.add(CourseUnit("Internet Programming","", ""))
        units.add(CourseUnit("Database Systems 2","", ""))
        units.add(CourseUnit("Entrepreneurship","", ""))
        units.add(CourseUnit("Network Management","", ""))
        units.add(CourseUnit("ICT Project Management","", ""))
        units.add(CourseUnit("Research Methodology","", ""))
        units.add(CourseUnit("Design and Analysis of Algorithm","", ""))
        units.add(CourseUnit("Operation Research","", ""))
        units.add(CourseUnit("Human Computer Interaction","", ""))
        units.add(CourseUnit("Security and Cryptography","", ""))
        units.add(CourseUnit("Mobile Application Development","", ""))
        units.add(CourseUnit("Advanced Business Data Structures and Algo","", ""))
        units.add(CourseUnit("Information Systems Audit","", ""))
        units.add(CourseUnit("Computer Graphics","", ""))
        units.add(CourseUnit("Mobile Computing","", ""))
        units.add(CourseUnit("Artificial Intelligence","", ""))
        units.add(CourseUnit("Distributed Systems","", ""))
        units.add(CourseUnit("ICT In Business And Society","", ""))
        units.add(CourseUnit("Total Quality Management","", ""))
        units.add(CourseUnit("Network Programming","", ""))
        units.add(CourseUnit("Distributed Systems","", ""))
        courses.add(Course("BIT","Information Technology", units))

        //BBIT
        units = ArrayList()
        units.add(CourseUnit("Basic Discrete Maths","", ""))
        units.add(CourseUnit("Data Communication","", ""))
        units.add(CourseUnit("Database systems","", ""))
        units.add(CourseUnit("Structured Programming","", ""))
        units.add(CourseUnit("Operating System and Design","", ""))
        courses.add(Course("BBIT","Business Information Technology", units))


    }


}