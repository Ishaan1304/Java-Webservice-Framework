class Student {
    constructor(rollNumber, name, age) {
        this.rollNumber=rollNumber;
        this.name=name;
        this.age=age;
    }

    setRollNumber(rollNumber) {
        this.rollNumber=rollNumber;
    }

    getRollNumber() {
        return this.rollNumber;
    }

    setName(name) {
        this.name=name;
    }

    getName() {
        return this.name;
    }

    setAge(age) {
        this.age=age;
    }

    getAge() {
        return this.age;
    }

}
