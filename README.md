[Leia esta página em Português do Brasil](README.pt-BR.md)

# 📨 Requests-Common

**Requests-Common** is a lightweight Java library that gives your services a shared language for dealing with incoming requests and outgoing responses. It ships with strongly typed envelopes, pagination helpers, credential handling, error contracts, and optional Spring Boot auto-configuration so that every microservice in your ecosystem can exchange data in a predictable way.

## 📚 Table of Contents
- [Why Requests-Common?](#-why-requests-common)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Core Building Blocks](#-core-building-blocks)
  - [Requests](#requests)
  - [Responses](#responses)
  - [User Credentials](#user-credentials)
  - [Pagination](#pagination)
  - [Validation](#validation)
  - [Error Handling](#error-handling)
- [Spring Boot Auto-configuration](#-spring-boot-auto-configuration)
- [End-to-end REST Example](#-end-to-end-rest-example)
- [Building from Source](#-building-from-source)
- [License](#-license)
- [Contact](#-contact)

---

## 🤔 Why Requests-Common?

When every service in a distributed system invents its own request and response wrappers, you end up re-implementing the same features repeatedly (IDs, headers, pagination metadata, error payloads, etc.). Requests-Common centralizes those patterns so you can:

* share DTO contracts across JVM services without copy/paste;
* automatically enrich responses with metadata such as `responseId`, payload size, and pagination totals;【F:src/main/java/dev/nishisan/requests/common/response/AbsResponse.java†L34-L114】
* attach credentials, trace IDs, and custom headers to every request in a consistent manner;【F:src/main/java/dev/nishisan/requests/common/request/AbsRequest.java†L30-L97】
* expose uniform error objects and HTTP statuses across your APIs.【F:src/main/java/dev/nishisan/requests/common/dto/ApiErrorDTO.java†L23-L78】【F:src/main/java/dev/nishisan/requests/common/exception/BasicException.java†L10-L90】

---

## 📦 Installation

Add the dependency to your build tool. Replace `1.2.12` with the most recent release if necessary.

<details>
<summary><strong>Maven</strong></summary>

```xml
<dependency>
    <groupId>dev.nishisan</groupId>
    <artifactId>requests-common</artifactId>
    <version>1.2.15</version>
</dependency>
```

</details>

<details>
<summary><strong>Gradle (Kotlin DSL)</strong></summary>

```kotlin
dependencies {
    implementation("dev.nishisan:requests-common:1.2.15")
}
```

</details>

<details>
<summary><strong>Gradle (Groovy DSL)</strong></summary>

```groovy
dependencies {
    implementation 'dev.nishisan:requests-common:1.2.15'
}
```

</details>

---

## ⚡ Quick Start

The snippet below demonstrates the typical flow of modelling a product creation endpoint.

```java
// 1. Define your payload
public record ProductDTO(String sku, String name, double price) {}

// 2. Wrap the payload in a request
public class CreateProductRequest extends AbsRequest<ProductDTO> {
    public CreateProductRequest(ProductDTO payload) {
        super(payload);
    }
}

// 3. Wrap the payload in a response
public class ProductResponse extends AbsResponse<ProductDTO> {
    public ProductResponse(ProductDTO payload) {
        super(payload);
        setStatusCode(201);
    }
}

// 4. Use the envelopes inside your service
@Service
public class ProductService {
    public ProductResponse create(CreateProductRequest request) {
        // access metadata
        var credentials = request.getUserCredential();
        var traceId = request.getTraceId();

        // perform your domain logic
        ProductDTO saved = saveProduct(request.getPayload());

        // build a standardized response
        ProductResponse response = new ProductResponse(saved);
        response.setSourceRequestId(request.getRequestId());
        response.setTraceId(traceId);
        response.addResponseHeader("X-Correlation-Id", traceId);
        return response;
    }
}
```

> 💡 `AbsResponse` automatically generates a `responseId` and calculates payload metadata such as collection size or total pages for `Page<?>` objects.【F:src/main/java/dev/nishisan/requests/common/response/AbsResponse.java†L42-L101】

---

## 🧱 Core Building Blocks

### Requests

* `AbsRequest<T>` implements `IRequest<T>` and ships with fields for `requestId`, `traceId`, payload, arbitrary headers, and a user credential object.【F:src/main/java/dev/nishisan/requests/common/request/AbsRequest.java†L30-L97】
* Use `setRequestId`, `setTraceId`, and `addRequestHeader` to pass metadata downstream; they are stored internally in a thread-safe `ConcurrentHashMap` for safe reuse across threads.【F:src/main/java/dev/nishisan/requests/common/request/AbsRequest.java†L30-L79】

### Responses

* `AbsResponse<T>` implements `IResponse<T>` and is responsible for
  * generating a unique `responseId` with `UUID.randomUUID()`;
  * keeping a `sourceRequestId`, `traceId`, and HTTP `statusCode`;
  * capturing payload size for `List`, `Map`, and `Page` payloads;
  * storing arbitrary response headers.【F:src/main/java/dev/nishisan/requests/common/response/AbsResponse.java†L34-L114】
* `setStatusCode` is especially useful together with the Spring Boot advice, which will propagate it to the HTTP layer automatically.【F:src/main/java/dev/nishisan/requests/common/spring/servlet/ResponseStatusAdvice.java†L22-L72】

### User Credentials

* Define your own credential classes by extending `AbsUserCredential<T>` or reusing `GenericUserCredential` when you just need an ID and optional opaque data.【F:src/main/java/dev/nishisan/requests/common/uc/AbsUserCredential.java†L22-L70】【F:src/main/java/dev/nishisan/requests/common/uc/GenericUserCredential.java†L17-L32】
* Attach them to a request via `request.setUserCredential(...)` and retrieve them later to authorize operations.【F:src/main/java/dev/nishisan/requests/common/request/AbsRequest.java†L32-L97】

### Pagination

* `AbsPageableRequest` creates a `PageableDTO` payload for you, so your controller only needs to extend it and pass the pagination parameters.【F:src/main/java/dev/nishisan/requests/common/request/AbsPageableRequest.java†L24-L38】
* `PageableDTO` stores `page`, `size`, `sort`, `direction`, and an optional free-text `query`, covering the majority of pagination use cases.【F:src/main/java/dev/nishisan/requests/common/dto/PageableDTO.java†L23-L94】

### Validation

* Use `@RequiredField` to mark DTO fields that must be present. You can optionally restrict values with nested `@AllowedValue` declarations (e.g., enum-like constraints).【F:src/main/java/dev/nishisan/requests/common/annotations/RequiredField.java†L1-L15】
* Because the annotation retention is `RUNTIME`, you can reflect on it in service or validation layers to enforce custom rules.【F:src/main/java/dev/nishisan/requests/common/annotations/RequiredField.java†L5-L15】

### Error Handling

* Extend `BasicException` for domain-specific errors. It keeps a reference to the original `IRequest`, a customizable HTTP status code, and a mutable `details` map that you can enrich fluently using `details(key, value)` in your subclass.【F:src/main/java/dev/nishisan/requests/common/exception/BasicException.java†L10-L90】
* Serialize errors uniformly with `ApiErrorDTO`, which carries the message, class name, status code, details, and optionally the request snapshot.【F:src/main/java/dev/nishisan/requests/common/dto/ApiErrorDTO.java†L23-L78】

---

## 🌷 Spring Boot Auto-configuration

Requests-Common provides an auto-configuration that registers `ResponseStatusAdvice` in servlet-based Spring Boot applications. When enabled, any controller method returning an `AbsResponse` (or throwing a `BasicException`) will have its HTTP status automatically synchronized with the value set inside the object.【F:src/main/java/dev/nishisan/requests/common/spring/servlet/ResponseStatusAdvice.java†L22-L72】【F:src/main/java/dev/nishisan/requests/common/spring/servlet/config/NishiRequestsCommonAutoConfiguration.java†L1-L22】

Enable it with the property below:

```properties
# application.properties
nishi.requests.common.enabled=true
```

Or in YAML:

```yaml
# application.yml
nishi:
  requests:
    common:
      enabled: true
```

Because the configuration is conditional on a servlet web application, you can safely add the dependency to libraries and non-web services without activating the advice.【F:src/main/java/dev/nishisan/requests/common/spring/servlet/config/NishiRequestsCommonAutoConfiguration.java†L5-L22】

---

## 🔁 End-to-end REST Example

The following controller illustrates how the pieces fit together in a Spring Boot project:

```java
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponse create(@RequestBody ProductDTO dto,
                                  @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        CreateProductRequest request = new CreateProductRequest(dto);
        request.setRequestId(UUID.randomUUID().toString());
        request.setTraceId(traceId);
        request.setUserCredential(new GenericUserCredential("user-123"));
        request.addRequestHeader("X-Trace-Id", traceId);

        return service.create(request);
    }

    @GetMapping
    public AbsResponse<Page<ProductDTO>> list(@RequestParam int page, @RequestParam int size) {
        ListProductsRequest request = new ListProductsRequest(page, size);
        AbsResponse<Page<ProductDTO>> response = service.list(request);
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/{sku}")
    public ProductResponse findBySku(@PathVariable String sku) throws ProductNotFoundException {
        return service.findBySku(sku);
    }
}
```

Error handling with a custom `BasicException` subclass:

```java
public class ProductNotFoundException extends BasicException {
    public ProductNotFoundException(String sku, IRequest<?> request) {
        super("Product not found", request, 404, Map.of("sku", sku));
    }

    @Override
    public ProductNotFoundException details(String key, Object value) {
        addDetail(key, value);
        return this;
    }
}

@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handle(ProductNotFoundException ex) {
        ApiErrorDTO error = new ApiErrorDTO();
        error.setMsg(ex.getMessage());
        error.setStatusCode(ex.getStatusCode());
        error.setClassName(ex.getClass().getName());
        error.setDetails(ex.details());
        error.setRequest(ex.getRequest());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }
}
```

With auto-configuration enabled, `ResponseStatusAdvice` ensures the HTTP status reflects the value set on the `AbsResponse` or `BasicException`, so there is no need to set it manually in each controller.【F:src/main/java/dev/nishisan/requests/common/spring/servlet/ResponseStatusAdvice.java†L22-L72】

---

## 🛠️ Building from Source

Clone the repository and run:

```bash
mvn clean package
```

The project targets Java 21 and Spring Boot 3.5, so ensure those toolchains are available in your environment.【F:pom.xml†L40-L48】

---

## 📄 License

This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html). See the `LICENSE` file for details.

---

## 📬 Contact

Questions or feedback? Open an issue or reach out via `github@nishisan.dev`.
