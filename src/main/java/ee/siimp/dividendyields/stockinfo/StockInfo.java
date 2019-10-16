package ee.siimp.dividendyields.stockinfo;

import ee.siimp.dividendyields.common.entity.BaseEntity;
import ee.siimp.dividendyields.stock.Stock;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Väärtpaberite splittimine
 * <p>
 * on aktsiate nimiväärtuse vähendamine aktsiakapitali suuruse muutmiseta,
 * muutub aktsiate arv. Uue nimiväärtusega aktsiatele antakse uus ISIN kood.
 * Aktsiate splittimist võivad kasutada äriühingud,
 * kelle väärtpaberid on suure nimiväärtusega ja seetõttu vähelikviidsed,
 * kuid kelle väärtpaberite vastu oodatakse suurema huvi tekkimist.
 * Splittimine lihtsustab muudatuste tegemist omanike struktuuris ka börsil mittenoteeritud äriühingutel.
 */
@Entity
@Getter
@Setter
class StockInfo extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, unique = true)
    private Stock stock;

    @NotNull
    @Min(0)
    @Column(precision = 21)
    private BigInteger numberOfSecurities;
}
