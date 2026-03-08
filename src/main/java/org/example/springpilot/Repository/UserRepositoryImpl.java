package org.example.springpilot.Repository;

import org.example.springpilot.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserForSA(){
        //getting users whose email exists and has opted for sentiment analysis
        Query query = new Query();
        /*
        query.addCriteria(Criteria.where("username").is("admin"));
        query.addCriteria(Criteria.where("field value").gte("value"));
        this is the query which can be used to filter for field value that is greater than value
        gte is for greater than or equal to and lte is for less than or equal to
         */
        /*
        query.addCriteria(Criteria.where("email").exists(true));
        query.addCriteria(Criteria.where("email").ne("null").ne(""));
         */

        //to make sure that the email is a valid email we will use the below regex
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));

/*        we can also do the above two queries by using and/or operator
        Criteria criteria = new Criteria();
        query.addCriteria(criteria.andOperator(Criteria.where("email").exists(true),Criteria.where("sentimentAnalysis").is(true)));

        we can also use this method to do this.
 */

        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }
}
