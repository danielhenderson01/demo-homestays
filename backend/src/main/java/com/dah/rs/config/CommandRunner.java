package com.dah.rs.config;

import static com.dah.rs.property.Amenity.DISHWASHER;
import static com.dah.rs.property.Amenity.KITCHEN;
import static com.dah.rs.property.Amenity.LAUNDRY;
import static com.dah.rs.property.Amenity.SMART_TV;
import static com.dah.rs.property.Amenity.WIFI;
import static com.dah.rs.property.Category.APARTMENT;
import static com.dah.rs.property.Category.CABIN;
import static com.dah.rs.property.Category.CONDO;
import static com.dah.rs.property.Category.COTTAGE;
import static com.dah.rs.property.Category.HOUSE;
import static com.dah.rs.property.Category.STUDIO;
import static com.dah.rs.property.USState.CALIFORNIA;
import static com.dah.rs.property.USState.COLORADO;
import static com.dah.rs.property.USState.FLORIDA;
import static com.dah.rs.property.USState.GEORGIA;
import static com.dah.rs.property.USState.ILLINOIS;
import static com.dah.rs.property.USState.MASSACHUSETTS;
import static com.dah.rs.property.USState.MONTANA;
import static com.dah.rs.property.USState.NEVADA;
import static com.dah.rs.property.USState.NEW_YORK;
import static com.dah.rs.property.USState.NORTH_CAROLINA;
import static com.dah.rs.property.USState.OREGON;
import static com.dah.rs.property.USState.TENNESSEE;
import static com.dah.rs.property.USState.TEXAS;
import static com.dah.rs.property.USState.WASHINGTON;

import com.dah.rs.image.ImageService;
import com.dah.rs.image.PropertyImage;
import com.dah.rs.image.PropertyImageRepository;
import com.dah.rs.property.Category;
import com.dah.rs.property.Property;
import com.dah.rs.property.PropertyRepository;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("seed")
@Component
@RequiredArgsConstructor
public class CommandRunner implements CommandLineRunner {

  @Value("${hs.seed.host-sub}")
  private String hostSub;

  @Value("${hs.seed.host-name}")
  private String hostName;

  @Value("${hs.seed.host-email}")
  private String hostEmail;

  private final ImageService imageService;
  private final AppUserRepository appUserRepository;
  private final PropertyRepository propertyRepository;
  private final PropertyImageRepository imageRepository;

  @Override
  @Transactional
  public void run(String @NonNull ... args) {
    if (
      appUserRepository.count() > 0
      || propertyRepository.count() > 0
      || imageRepository.count() > 0
    ) {
      return;
    }

    AppUser host = appUserRepository.save(
      AppUser.builder()
        .cognitoSub(hostSub)
        .name(hostName)
        .email(hostEmail)
        .role(AppRole.HOST)
        .build()
    );

    Property p1 = Property.builder()
      .name("Sunnyvale Apartments")
      .host(host)
      .category(APARTMENT)
      .description(
        "Modern apartments with ample natural light and open layouts. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Where comfort meets convenience.")
      .street("123 Elm Street")
      .city("Sunnyvale")
      .state(CALIFORNIA)
      .zipcode("94085")
      .beds(2)
      .baths(2)
      .squareFeet(1100)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p1);

    Property p2 = Property.builder()
      .name("Hillside Villas")
      .host(host)
      .category(Category.OTHER)
      .description(
        "Luxurious villas located in the serene hills. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Experience tranquility like never before.")
      .street("456 Oak Drive")
      .city("Boulder")
      .state(COLORADO)
      .zipcode("80302")
      .beds(4)
      .baths(3)
      .squareFeet(2500)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p2);

    Property p3 = Property.builder()
      .name("Urban Studio Lofts")
      .host(host)
      .category(STUDIO)
      .description(
        "Chic studio apartments in the heart of downtown. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Urban living redefined.")
      .street("789 Pine Avenue")
      .city("New York")
      .state(NEW_YORK)
      .zipcode("10001")
      .beds(1)
      .baths(1)
      .squareFeet(600)
      .price(BigDecimal.valueOf(150))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p3);

    Property p4 = Property.builder()
      .name("Beachfront Retreat")
      .host(host)
      .category(CONDO)
      .description(
        "A beautiful condo with a stunning ocean view. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Wake up to the waves.")
      .street("321 Ocean Boulevard")
      .city("Miami")
      .state(FLORIDA)
      .zipcode("33139")
      .beds(3)
      .baths(2)
      .squareFeet(1800)
      .price(BigDecimal.valueOf(120))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p4);

    Property p5 = Property.builder()
      .name("Greenfield Estate")
      .host(host)
      .category(HOUSE)
      .description(
        "Spacious family home with a large backyard. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("The perfect home for your family.")
      .street("654 Maple Lane")
      .city("Austin")
      .state(TEXAS)
      .zipcode("78704")
      .beds(5)
      .baths(4)
      .squareFeet(3200)
      .price(BigDecimal.valueOf(140))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p5);

    Property p6 = Property.builder()
      .name("Downtown Penthouse")
      .host(host)
      .category(Category.OTHER)
      .description(
        "High-end penthouse with stunning city views. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Luxury living at its peak.")
      .street("111 Main Street")
      .city("Chicago")
      .state(ILLINOIS)
      .zipcode("60601")
      .beds(3)
      .baths(3)
      .squareFeet(2200)
      .price(BigDecimal.valueOf(120))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p6);

    Property p7 = Property.builder()
      .name("Country Cottage")
      .host(host)
      .category(COTTAGE)
      .description(
        "Cozy and charming cottage in the countryside. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Escape the hustle and bustle.")
      .street("202 Birch Road")
      .city("Nashville")
      .state(TENNESSEE)
      .zipcode("37201")
      .beds(2)
      .baths(2)
      .squareFeet(1400)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p7);

    Property p8 = Property.builder()
      .name("Lakeside Cabin")
      .host(host)
      .category(CABIN)
      .description(
        "Rustic cabin overlooking a peaceful lake. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Nature at your doorstep.")
      .street("505 Lakeview Drive")
      .city("Lake Tahoe")
      .state(NEVADA)
      .zipcode("89451")
      .beds(3)
      .baths(2)
      .squareFeet(2000)
      .price(BigDecimal.valueOf(110))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p8);

    Property p9 = Property.builder()
      .name("Midtown Townhomes")
      .host(host)
      .category(Category.OTHER)
      .description(
        "Stylish and modern townhomes near all amenities. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Urban sophistication.")
      .street("404 Cedar Street")
      .city("Atlanta")
      .state(GEORGIA)
      .zipcode("30303")
      .beds(3)
      .baths(2)
      .squareFeet(1700)
      .price(BigDecimal.valueOf(130))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p9);

    Property p10 = Property.builder()
      .name("Garden Terrace")
      .host(host)
      .category(CONDO)
      .description(
        "Elegant condos surrounded by lush greenery. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("A green paradise in the city.")
      .street("303 Flower Lane")
      .city("Portland")
      .state(OREGON)
      .zipcode("97201")
      .beds(2)
      .baths(2)
      .squareFeet(1400)
      .price(BigDecimal.valueOf(160))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p10);

    Property p11 = Property.builder()
      .name("Aspen Chalet")
      .host(host)
      .category(Category.OTHER)
      .description(
        "A cozy chalet nestled in the mountains. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Your winter wonderland.")
      .street("707 Snowy Path")
      .city("Aspen")
      .state(COLORADO)
      .zipcode("81611")
      .beds(4)
      .baths(3)
      .squareFeet(2300)
      .price(BigDecimal.valueOf(120))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p11);

    Property p12 = Property.builder()
      .name("Parkside Flats")
      .host(host)
      .category(APARTMENT)
      .description(
        "Affordable apartments near the park. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Simple living, close to nature.")
      .street("101 Park Avenue")
      .city("Seattle")
      .state(WASHINGTON)
      .zipcode("98101")
      .beds(1)
      .baths(1)
      .squareFeet(800)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p12);

    Property p13 = Property.builder()
      .name("Suburban Sanctuary")
      .host(host)
      .category(HOUSE)
      .description(
        "Family-friendly home in a quiet neighborhood. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Your peaceful haven.")
      .street("303 Suburb Drive")
      .city("Charlotte")
      .state(NORTH_CAROLINA)
      .zipcode("28202")
      .beds(4)
      .baths(3)
      .squareFeet(2600)
      .price(BigDecimal.valueOf(150))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p13);

    Property p14 = Property.builder()
      .name("Harbor View Lofts")
      .host(host)
      .category(CONDO)
      .description(
        "Modern lofts with breathtaking harbor views. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Life by the water.")
      .street("606 Dockside Way")
      .city("San Diego")
      .state(CALIFORNIA)
      .zipcode("92101")
      .beds(2)
      .baths(2)
      .squareFeet(1500)
      .price(BigDecimal.valueOf(150))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p14);

    Property p15 = Property.builder()
      .name("Historic Brownstone")
      .host(host)
      .category(CONDO)
      .description(
        "Classic brownstone with modern updates. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Timeless elegance.")
      .street("888 Heritage Street")
      .city("Boston")
      .state(MASSACHUSETTS)
      .zipcode("02108")
      .beds(3)
      .baths(3)
      .squareFeet(2000)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p15);

    Property p16 = Property.builder()
      .name("The Redwood Cabin")
      .host(host)
      .category(CABIN)
      .description(
        "Secluded cabin surrounded by towering redwoods. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Your forest escape.")
      .street("909 Redwood Lane")
      .city("Carmel")
      .state(CALIFORNIA)
      .zipcode("93921")
      .beds(2)
      .baths(2)
      .squareFeet(1200)
      .price(BigDecimal.valueOf(90))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p16);

    Property p17 = Property.builder()
      .name("Ocean Breeze Bungalow")
      .host(host)
      .category(COTTAGE)
      .description(
        "Charming bungalow just steps from the beach. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Beach living made easy.")
      .street("202 Seaside Lane")
      .city("Santa Monica")
      .state(CALIFORNIA)
      .zipcode("90401")
      .beds(3)
      .baths(2)
      .squareFeet(1600)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p17);

    Property p18 = Property.builder()
      .name("Downtown Skyline Tower")
      .host(host)
      .category(CONDO)
      .description(
        "Luxury penthouse in the heart of downtown. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("Sophistication in the city.")
      .street("121 High Street")
      .city("Dallas")
      .state(TEXAS)
      .zipcode("75201")
      .beds(4)
      .baths(4)
      .squareFeet(3500)
      .price(BigDecimal.valueOf(160))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p18);

    Property p19 = Property.builder()
      .name("Meadow View Ranch")
      .host(host)
      .category(HOUSE)
      .description(
        "Expansive ranch property with stunning views. Lorem ipsum dolor sit amet, consectetur adipisicing elit. A accusantium culpa delectus deleniti, dicta eius excepturi exercitationem fugiat illum odio qui quod rerum similique. A dolore dolorem esse eveniet explicabo illo impedit ipsum mollitia nesciunt nihil pariatur quas, repellendus sed soluta tempora tempore ullam? Accusamus aperiam atque commodi cumque eaque, est incidunt iste, non pariatur perspiciatis quas quis reiciendis saepe sint velit voluptas voluptates. Accusantium animi commodi distinctio eaque ex harum ipsum minus mollitia nemo obcaecati recusandae reiciendis, sunt vel! Accusamus, blanditiis consequuntur corporis distinctio ducimus et facilis hic id illum iste maiores quae quis, quod saepe similique soluta suscipit.")
      .tagline("A countryside retreat.")
      .street("515 Open Plains Road")
      .city("Bozeman")
      .state(MONTANA)
      .zipcode("59715")
      .beds(5)
      .baths(4)
      .squareFeet(4000)
      .price(BigDecimal.valueOf(170))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .build();

    propertyRepository.save(p19);

    savePropertyImages(p1, List.of("a", "b", "c"));
    savePropertyImages(p2, List.of("b", "c", "d"));
    savePropertyImages(p3, List.of("c", "d", "e"));
    savePropertyImages(p4, List.of("d", "e", "f"));
    savePropertyImages(p5, List.of("e", "f", "g"));
    savePropertyImages(p6, List.of("f", "g", "h"));
    savePropertyImages(p7, List.of("g", "h", "i"));
    savePropertyImages(p8, List.of("h", "i", "j"));
    savePropertyImages(p9, List.of("i", "j", "k"));
    savePropertyImages(p10, List.of("j", "k", "l"));
    savePropertyImages(p11, List.of("k", "l", "m"));
    savePropertyImages(p12, List.of("l", "m", "n"));
    savePropertyImages(p13, List.of("m", "n", "o"));
    savePropertyImages(p14, List.of("n", "o", "p"));
    savePropertyImages(p15, List.of("o", "p", "q"));
    savePropertyImages(p16, List.of("p", "q", "r"));
    savePropertyImages(p17, List.of("q", "r", "s"));
    savePropertyImages(p18, List.of("r", "s", "a"));
    savePropertyImages(p19, List.of("s", "a", "b"));
  }

  private void savePropertyImages(Property property, List<String> filenames) {
    String s3Prefix = imageService.getS3SamplePrefix();
    String cloudFrontUrl = imageService.getCloudFrontUrl();
    for (String name : filenames) {
      String s3Key = s3Prefix + "/" + name + ".jpg";
      PropertyImage build = PropertyImage
        .builder()
        .property(property)
        .imageUrl(cloudFrontUrl + "/" + s3Key)
        .objectKey(s3Key)
        .build();
      imageRepository.save(build);
    }
  }

}
