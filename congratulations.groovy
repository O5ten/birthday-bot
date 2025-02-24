import groovy.json.*
import groovy.yaml.YamlSlurper

def getDate() {
    def date = java.time.Instant.now().toString().tokenize('T')[0].tokenize('-')
    return [year: date[0] as int, month: date[1] as int, day: date[2] as int]
}

def getBirthdayPerson() {
    def date = getDate()
    def people = new YamlSlurper().parseText(System.getenv("PERSONAL_DATA")).people
    def potentialPersons = people.findAll {p -> p.date.month == date.month && p.date.day == date.day}
    if(potentialPersons) {
        potentialPersons.each { p -> 
            println "$p.date.year && $date.year"
            p.age = date.year - p.date.year    
        }
         
    }
    return potentialPersons
}

def MICKE='7cnjxi1g6bdi8ffmo6tz3mc3co'
def PI_DELAR='e3pb6xqy1b87tfuze691at7ajo'

def botBearerToken = System.getenv('BOT_BEARER_TOKEN')

def persons = getBirthdayPerson();
if(persons) {
    def ageString = persons.size() == 1 ? 
            persons[0].age : 
            (persons[0].age + ' och ' + persons[1].age)
    def nameString = persons.size() == 1 ? 
            persons[0].name : 
            (persons[0].name + ' och ' + persons[1].name)
    """curl -k -H "Authorization: Bearer $botBearerToken" https://mattermost.05ten.se/api/v4/posts -d '{"channel_id": "$PI_DELAR", "message": "@all Grattis på födelsedagen **$nameString** som fyller **$ageString** år idag!"}'""".execute()
} else {
    println "Nobody has a birthday today. :'("
}
