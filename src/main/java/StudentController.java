import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StudentController {
    private static EntityManagerFactory emf;
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        emf = Persistence.createEntityManagerFactory("sujan"); // replace with your persistence unit name

        while (true) {
            System.out.println("Zoo Application");
            
            System.out.println("1. Add Animal");
            System.out.println("2. Remove Animal");
            System.out.println("3. update Animals");
            System.out.println("4.  Animal query");
            System.out.println("6.  category wise animals");
            System.out.println("7.  age wise animals (between )");
            System.out.println("8.  animalsWithNameStartingWithL");

            System.out.println("5. Add Zoo");


            System.out.println("0. Quit");

            System.out.print("Enter your choice: ");
            String choice = reader.readLine();

            switch (choice) {
                case "1":
                    addAnimal();
                    break;
                case "2":
                    removeAnimal();
                    break;
                case "3":
                    updateAnimal();
                    break;
                case "4":
                    animalquery();
                    break;
                case "5":
                    addZoo();
                    break;
                case "6":
                    categoryWiseAnimals();
                    break;
                case "7":
                    ageWiseAnimals();
                    break;

                case "8":
                    animalsWithNameStartingWithL();
                case "0":
                    emf.close();
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
    }
    private static void animalsWithNameStartingWithL() {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Animal> cq = cb.createQuery(Animal.class);
        Root<Animal> root = cq.from(Animal.class);

        cq.where(cb.like(root.<String>get("animalName"), "L%"));

        List<Animal> animals = em.createQuery(cq).getResultList();

        if (animals.isEmpty()) {
            System.out.println("No animals found with names starting with 'L'.");
        } else {
            System.out.println("Animals with names starting with 'L':");
            for (Animal animal : animals) {
                System.out.println(" " + animal.getAnimalName() + ", age: " + animal.getAge());
            }
        }

        em.close();
    }

    private static void ageWiseAnimals() throws IOException {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Animal> cq = cb.createQuery(Animal.class);
        Root<Animal> root = cq.from(Animal.class);

        cq.where(cb.between(root.<Comparable>get("age"), 2, 4));

        List<Animal> animals = em.createQuery(cq).getResultList();

        if (animals.isEmpty()) {
            System.out.println("No animals found in the given age range.");
        } else {
            System.out.println("Animals with age between 2 to 5 years:");
            for (Animal animal : animals) {
                System.out.println(" " + animal.getAnimalName() + ", age: " + animal.getAge());
            }
        }

        em.close();
    }


    private static void animalquery() throws IOException {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Animal> cq = cb.createQuery(Animal.class);
        Root<Animal> animalRoot = cq.from(Animal.class);

        cq.select(animalRoot).where(cb.greaterThan(animalRoot.<Comparable>get("age"),4));

        TypedQuery<Animal> query = em.createQuery(cq);
        List<Animal> animals = query.getResultList();

        for (Animal animal : animals) {
            System.out.println("Animal name: " + animal.getAnimalName() + ", age: " + animal.getAge());
        }

        em.close();
    }



    private static void categoryWiseAnimals() throws IOException {
        EntityManager em = emf.createEntityManager();
        System.out.print("Enter animal category: ");
        String animalCategory = reader.readLine();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Animal> cq = cb.createQuery(Animal.class);
        Root<Animal> animalRoot = cq.from(Animal.class);
        cq.select(animalRoot).where(cb.equal(animalRoot.get("category"), animalCategory));

        TypedQuery<Animal> query = em.createQuery(cq);
        List<Animal> animals = query.getResultList();

        if (animals.isEmpty()) {
            System.out.println("No animals found in the given category.");
        } else {
            System.out.println("Animals in category " + animalCategory + ":");
            for (Animal animal : animals) {
                System.out.println("- " + animal.getAnimalName() + ", age: " + animal.getAge());
            }
        }

        em.close();
    }


    private static void updateAnimal() throws IOException {
        EntityManager em = emf.createEntityManager();

        // Start a new transaction
        em.getTransaction().begin();

        // Read input from the user
        System.out.print("Enter animal id: ");
        int animalId = Integer.parseInt(reader.readLine());
        System.out.print("Enter new age: ");
        String newAge = reader.readLine();

        // Retrieve the Animal object to update
        Animal animal = em.find(Animal.class, animalId);

        // Update the age property
        animal.setAge(newAge);

        // Persist the updated Animal object to the database
        em.merge(animal);

        // Commit the transaction and close the EntityManager
        em.getTransaction().commit();
        em.close();

        System.out.println("Animal " + animalId + " updated successfully.");
    }
    private static void removeAnimal() throws IOException {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        System.out.print("Enter Animal ID: ");
        int animalId = Integer.parseInt(reader.readLine());

        Animal animal = em.find(Animal.class, animalId);
        if (animal == null) {
            System.out.println("Animal not found!");
        } else {
            em.remove(animal);
            System.out.println("Animal with ID " + animalId + " deleted successfully.");
        }

        em.getTransaction().commit();
        em.close();
    }

    private static void addAnimal() throws IOException {

        EntityManager em = emf.createEntityManager();
        // Start a new transaction
        em.getTransaction().begin();

        // Read input from the user
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter Animal name: ");
        String animalName = reader.readLine();
        System.out.print("Enter Animal Category: ");
        String animalCategory = reader.readLine();
        System.out.print("Enter Animal Age: ");
        String animalAge = reader.readLine();

        System.out.print("Enter Zoo ID or name: ");
        String zooInput = reader.readLine();
        Zoo zoo;
        try {
            int zooId = Integer.parseInt(zooInput);
            zoo = em.find(Zoo.class, zooId);
        } catch (NumberFormatException e) {
            zoo = new Zoo();
            zoo.setZname(zooInput);
        }


// Create a new Animal object and set its properties
        Animal animal = new Animal();
        animal.setAnimalName(animalName);
        animal.setCategory(animalCategory);
        animal.setAge(animalAge);
        animal.setZoo(zoo);


        // Persist the new Zoo object to the database
        em.persist(animal);

        // Commit the transaction and close the EntityManager
        em.getTransaction().commit();

        System.out.println("Animal "+animalName + " added successfully.");

    }
    private static void addZoo() throws IOException {
         {
            EntityManager em = emf.createEntityManager();
            // Start a new transaction
            em.getTransaction().begin();

            // Read input from the user
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter zoo name: ");
            String zooName = reader.readLine();

            // Create a new Zoo object and set its properties
            Zoo zoo = new Zoo();
            zoo.setZname(zooName);
//            zoo.setAnimals(new ArrayList<Animal>());

            // Persist the new Zoo object to the database
            em.persist(zoo);

            // Commit the transaction and close the EntityManager
            em.getTransaction().commit();

            System.out.println("Zoo " + zooName + " added successfully.");
        }
    }

}
